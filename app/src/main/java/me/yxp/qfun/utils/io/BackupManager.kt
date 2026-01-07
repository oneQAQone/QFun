package me.yxp.qfun.utils.io

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import org.json.JSONObject
import java.io.File

object BackupManager {
    private const val EXPORT_DIR_NAME = "backup"

    suspend fun performExport(context: Context, uri: Uri): Result<Boolean> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cacheDir = context.externalCacheDir ?: context.cacheDir
                val tempRoot = File(cacheDir, "qfun_export_tmp")
                FileUtils.delete(tempRoot)
                val backupDir = File(tempRoot, EXPORT_DIR_NAME)
                FileUtils.ensureDir(backupDir)

                val configFile = File(backupDir, "config.json")
                val prefsData = JSONObject()
                BaseSwitchHookItem.prefs.all.forEach { (k, v) ->
                    prefsData.put(k, v)
                }
                prefsData.put("_version_code", BuildConfig.VERSION_CODE)
                prefsData.put("_uin", QQCurrentEnv.currentUin)
                FileUtils.writeText(configFile, prefsData.toString())

                val currentEnvDir = File(QQCurrentEnv.currentDir)
                if (currentEnvDir.exists() && currentEnvDir.isDirectory) {
                    val dataDir = File(backupDir, "data")
                    FileUtils.ensureDir(dataDir)
                    currentEnvDir.listFiles()?.forEach { file ->
                        if (file.name != "log" && file.name != "cache") {
                            val dest = File(dataDir, file.name)
                            if (file.isDirectory) {
                                FileUtils.copy(file, dest)
                            } else {
                                file.copyTo(dest, true)
                            }
                        }
                    }
                }

                val zipFile = File(tempRoot, "export.zip")
                FileUtils.zip(backupDir, zipFile)

                context.contentResolver.openOutputStream(uri)?.use { os ->
                    zipFile.inputStream().use { it.copyTo(os) }
                }
                FileUtils.delete(tempRoot)

            }.onFailure { e ->
                LogUtils.e("ExportConfig", e)
            }
        }

    suspend fun performImport(context: Context, uri: Uri): Result<Boolean> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cacheDir = context.externalCacheDir ?: context.cacheDir
                val tempRoot = File(cacheDir, "qfun_import_tmp")
                FileUtils.delete(tempRoot)
                FileUtils.ensureDir(tempRoot)

                val zipFile = File(tempRoot, "import.zip")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    zipFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                FileUtils.unzip(zipFile, tempRoot)
                val backupDir = File(tempRoot, EXPORT_DIR_NAME)
                if (!backupDir.exists()) {
                    throw Exception("错误的备份文件格式：找不到 backup 目录")
                }

                val configFile = File(backupDir, "config.json")
                if (configFile.exists()) {
                    val jsonStr = FileUtils.readText(configFile)
                    if (jsonStr != null) {
                        val json = JSONObject(jsonStr)
                        BaseSwitchHookItem.prefs.edit(commit = true) {
                            clear()
                            val keys = json.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                if (key.startsWith("_")) continue
                                when (val value = json.get(key)) {
                                    is Boolean -> putBoolean(key, value)
                                    is String -> putString(key, value)
                                    is Int -> putInt(key, value)
                                    is Long -> putLong(key, value)
                                    is Float -> putFloat(key, value)
                                }
                            }
                        }
                    }
                }

                val dataDir = File(backupDir, "data")
                if (dataDir.exists() && dataDir.isDirectory) {
                    val targetDir = File(QQCurrentEnv.currentDir)
                    FileUtils.ensureDir(targetDir)
                    FileUtils.copy(dataDir, targetDir)
                }
                MainHook.processDataForCurrent("init")
                FileUtils.delete(tempRoot)

            }.onFailure { e ->
                LogUtils.e("ImportConfig", e)
            }
        }
}