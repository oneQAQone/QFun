package me.yxp.qfun.utils.json

import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object JsonConfigUtils {

    fun putString(absoluteDir: String, configName: String, key: String, value: String) {
        putValue(absoluteDir, configName, key, value)
    }

    fun putInt(absoluteDir: String, configName: String, key: String, value: Int) {
        putValue(absoluteDir, configName, key, value)
    }

    fun putBoolean(absoluteDir: String, configName: String, key: String, value: Boolean) {
        putValue(absoluteDir, configName, key, value)
    }

    fun putLong(absoluteDir: String, configName: String, key: String, value: Long) {
        putValue(absoluteDir, configName, key, value)
    }

    fun getString(
        absoluteDir: String,
        configName: String,
        key: String,
        defaultValue: String
    ): String {
        val json = loadConfig(absoluteDir, configName)
        return json.optString(key, defaultValue)
    }

    fun getInt(absoluteDir: String, configName: String, key: String, defaultValue: Int): Int {
        val json = loadConfig(absoluteDir, configName)
        return json.optInt(key, defaultValue)
    }

    fun getBoolean(
        absoluteDir: String,
        configName: String,
        key: String,
        defaultValue: Boolean
    ): Boolean {
        val json = loadConfig(absoluteDir, configName)
        return json.optBoolean(key, defaultValue)
    }

    fun getLong(absoluteDir: String, configName: String, key: String, defaultValue: Long): Long {
        val json = loadConfig(absoluteDir, configName)
        return json.optLong(key, defaultValue)
    }

    private fun putValue(absoluteDir: String, configName: String, key: String, value: Any) {
        val json = loadConfig(absoluteDir, configName)
        json.put(key, value)
        saveConfig(absoluteDir, configName, json)
    }

    private fun loadConfig(absoluteDir: String, configName: String): JSONObject {
        val configFile = getConfigFile(absoluteDir, configName)
        if (!configFile.exists()) {
            return JSONObject()
        }

        try {
            FileReader(configFile).use { fileReader ->
                BufferedReader(fileReader).use { reader ->
                    val contentBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        contentBuilder.append(line)
                    }
                    val content = contentBuilder.toString()
                    return JSONObject(JSONTokener(content))
                }
            }
        } catch (e: IOException) {
            throw Exception("Failed to load config: $configName", e)
        } catch (e: JSONException) {
            throw Exception("Failed to load config: $configName", e)
        }
    }

    private fun saveConfig(absoluteDir: String, configName: String, json: JSONObject) {
        val configFile = getConfigFile(absoluteDir, configName)
        try {
            FileWriter(configFile).use { writer ->
                writer.write(json.toString(4))
            }
        } catch (e: IOException) {
            throw Exception("Failed to save config: $configName", e)
        }
    }

    private fun getConfigFile(absoluteDir: String, configName: String): File {
        val dir = File(absoluteDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "$configName.json")
    }
}