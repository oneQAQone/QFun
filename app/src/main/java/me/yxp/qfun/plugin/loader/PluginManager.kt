package me.yxp.qfun.plugin.loader

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.plugin.bean.PluginInfo
import me.yxp.qfun.utils.reflect.TAG
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.io.ObjectStore
import me.yxp.qfun.utils.log.PluginError
import me.yxp.qfun.utils.qq.QQCurrentEnv
import java.io.File

object PluginManager {
    val plugins = mutableListOf<PluginInfo>()
    val autoLoadList = mutableListOf<String>()

    private val listSerializer = ListSerializer(String.serializer())

    private val pluginDir: File
        get() = File(QQCurrentEnv.currentDir, "plugin").apply { mkdirs() }

    fun loadAll() {
        val existingMap = plugins.associateBy { it.id }
        plugins.clear()

        pluginDir.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
            PluginInfo.fromDir(dir)?.let { newInfo ->
                val existing = existingMap[newInfo.id]
                if (existing != null) {
                    existing.updateFromDisk()
                    plugins.add(existing)
                } else {
                    plugins.add(newInfo)
                }
            }
        }

        val savedList = ObjectStore.load("data", "AutoLoadList", listSerializer)
        if (savedList != null) {
            autoLoadList.clear()
            autoLoadList.addAll(savedList)
        }
    }

    fun startPlugin(plugin: PluginInfo): Boolean {
        return try {
            plugin.compiler.start()
            true
        } catch (e: Exception) {
            PluginError.evalError(e, plugin)
            false
        }
    }

    fun stopPlugin(plugin: PluginInfo) {
        try {
            plugin.compiler.stop(true)
        } catch (e: Exception) {
            PluginError.evalError(e, plugin)
        }
    }

    fun reloadPlugin(plugin: PluginInfo): Boolean {
        stopPlugin(plugin)
        return startPlugin(plugin)
    }

    fun deletePlugin(plugin: PluginInfo) {
        stopPlugin(plugin)
        plugins.remove(plugin)
        autoLoadList.remove(plugin.id)
        FileUtils.delete(File(plugin.dirPath))
        saveConfig()
    }

    fun setAutoLoad(plugin: PluginInfo, isAuto: Boolean) {
        if (isAuto) {
            if (!autoLoadList.contains(plugin.id)) {
                autoLoadList.add(plugin.id)
            }
        } else {
            autoLoadList.remove(plugin.id)
        }
        saveConfig()
    }

    fun startAutoLoadPlugins() {
        ModuleScope.launchIO(TAG) {
            plugins.filter { autoLoadList.contains(it.id) }.forEach {
                if (!it.isRunning) {
                    startPlugin(it)
                }
                delay(100)
            }
        }
    }

    fun stopAllPlugins() {

        plugins.filter { it.isRunning }.forEach {
            stopPlugin(it)
        }

    }

    fun saveConfig() {
        ObjectStore.save("data", "AutoLoadList", autoLoadList.toList(), listSerializer)
    }

    fun autoStart() {
        startAutoLoadPlugins()
    }

    fun createExamplePlugin(): Boolean {
        val timestamp = System.currentTimeMillis()
        val dirName = "Example_$timestamp"
        val targetDir = File(pluginDir, dirName)

        if (!targetDir.mkdirs()) return false

        try {
            val propFile = File(targetDir, "info.prop")
            val propContent = """
                id=example_$timestamp
                pluginName=示例脚本
                versionCode=1.0
                author=QFunDeveloper
            """.trimIndent()
            FileUtils.writeText(propFile, propContent)

            val descFile = File(targetDir, "desc.txt")
            FileUtils.writeText(descFile, "这是一个自动生成的示例脚本。")

            val mainFile = File(targetDir, "main.java")
            val javaContent = """
                log("脚本开始运行...");
                qqToast(2, "Hello World!");
                
                addItem("测试菜单", "onTestClick");
                
                void onTestClick(int chatType, String peerUin, String peerName) {
                    qqToast(2, "点击了菜单");
                }
                
                void unLoadPlugin() {
                    qqToast(0, "脚本停止运行");
                    log("脚本停止运行");
                }
            """.trimIndent()
            FileUtils.writeText(mainFile, javaContent)
            PluginInfo.fromDir(targetDir)?.let { plugins.add(it) }
            return true
        } catch (_: Exception) {
            FileUtils.delete(targetDir)
            return false
        }
    }

    fun installPluginFromZip(zipFile: File): String? {
        val tempDir =
            File(QQCurrentEnv.currentDir, "cache/temp_install_${System.currentTimeMillis()}")

        try {
            if (!FileUtils.unzip(zipFile, tempDir)) {
                return "解压失败"
            }

            var scriptRoot = tempDir
            val files = tempDir.listFiles()
            if (files != null && files.size == 1 && files[0].isDirectory) {
                scriptRoot = files[0]
            }

            val newInfo = PluginInfo.fromDir(scriptRoot) ?: return "无效的脚本包(缺少info.prop)"

            val oldPlugin = plugins.find { it.id == newInfo.id }
            var wasRunning = false
            var finalTargetDir = File(pluginDir, scriptRoot.name)

            if (oldPlugin != null) {
                finalTargetDir = File(oldPlugin.dirPath)
                wasRunning = oldPlugin.isRunning

                if (wasRunning) {
                    oldPlugin.compiler.stop(true)
                }

                val oldConfigDir = File(finalTargetDir, "config")
                val tempConfigBackup =
                    File(QQCurrentEnv.currentDir, "cache/config_backup_${newInfo.id}")
                if (oldConfigDir.exists()) {
                    FileUtils.copy(oldConfigDir, tempConfigBackup)
                }

                FileUtils.delete(finalTargetDir)
                FileUtils.copy(scriptRoot, finalTargetDir)

                if (tempConfigBackup.exists()) {
                    val newConfigDir = File(finalTargetDir, "config")
                    FileUtils.ensureDir(newConfigDir)
                    FileUtils.copy(tempConfigBackup, newConfigDir)
                    FileUtils.delete(tempConfigBackup)
                }

            } else {
                if (finalTargetDir.exists()) {
                    finalTargetDir =
                        File(pluginDir, "${scriptRoot.name}_${System.currentTimeMillis()}")
                }
                FileUtils.copy(scriptRoot, finalTargetDir)
            }

            loadAll()

            val installedPlugin = plugins.find { it.id == newInfo.id }
            if (installedPlugin != null) {
                if (wasRunning || autoLoadList.contains(installedPlugin.id)) {
                    startPlugin(installedPlugin)
                }
            }

            return null

        } catch (e: Exception) {
            e.printStackTrace()
            return "安装异常: ${e.message}"
        } finally {
            FileUtils.delete(tempDir)
        }
    }
}
