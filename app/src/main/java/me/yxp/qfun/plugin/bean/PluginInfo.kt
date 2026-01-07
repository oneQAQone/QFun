package me.yxp.qfun.plugin.bean

import me.yxp.qfun.plugin.loader.PluginCompiler
import java.io.File
import java.io.InputStreamReader
import java.util.Properties

data class PluginInfo(
    val id: String,
    var name: String,
    var version: String,
    var author: String,
    val dirPath: String
) {
    var isRunning: Boolean = false
    val compiler: PluginCompiler = PluginCompiler(this)
    var desc: String = ""

    init {
        updateFromDisk()
    }

    fun updateFromDisk() {
        runCatching {
            val dir = File(dirPath)
            val propFile = File(dir, "info.prop")
            if (propFile.exists()) {
                val props = Properties()
                InputStreamReader(propFile.inputStream(), Charsets.UTF_8).use { reader ->
                    props.load(reader)
                }
                name = props.getProperty("pluginName", name)
                version = props.getProperty("versionCode", version)
                author = props.getProperty("author", author)
            }

            val descFile = File(dir, "desc.txt")
            desc = if (descFile.exists()) {
                descFile.readText()
            } else {
                ""
            }
        }
    }

    companion object {
        fun fromDir(dir: File): PluginInfo? {
            return runCatching {
                val propFile = File(dir, "info.prop")
                if (!propFile.exists()) return null

                val props = Properties()
                InputStreamReader(propFile.inputStream(), Charsets.UTF_8).use { reader ->
                    props.load(reader)
                }

                val id = props.getProperty("id") ?: return null

                PluginInfo(
                    id = id,
                    name = props.getProperty("pluginName", "Unknown"),
                    version = props.getProperty("versionCode", "1.0"),
                    author = props.getProperty("author", "Unknown"),
                    dirPath = dir.absolutePath
                )
            }.getOrNull()
        }
    }
}