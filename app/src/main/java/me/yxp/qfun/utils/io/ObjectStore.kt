package me.yxp.qfun.utils.io

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.yxp.qfun.utils.qq.QQCurrentEnv
import java.io.File

object ObjectStore {

    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    fun <T : Any> save(dir: String, fileName: String, obj: T, serializer: KSerializer<T>): Boolean {
        val file = File("${QQCurrentEnv.currentDir}$dir", fileName)
        return save(file, obj, serializer)
    }

    fun <T : Any> save(file: File, obj: T, serializer: KSerializer<T>): Boolean {
        if (!FileUtils.ensureFile(file)) return false
        return runCatching {
            file.writeText(json.encodeToString(serializer, obj))
            true
        }.getOrElse { false }
    }

    fun <T : Any> load(dir: String, fileName: String, serializer: KSerializer<T>): T? {
        val file = File("${QQCurrentEnv.currentDir}$dir", fileName)
        return load(file, serializer)
    }

    fun <T : Any> load(file: File, serializer: KSerializer<T>): T? {
        if (!file.exists()) return null
        return runCatching {
            json.decodeFromString(serializer, file.readText())
        }.getOrNull()
    }

    inline fun <reified T : Any> saveInline(dir: String, fileName: String, obj: T): Boolean {
        return save(dir, fileName, obj, serializer())
    }

    inline fun <reified T : Any> saveInline(file: File, obj: T): Boolean {
        return save(file, obj, serializer())
    }

    inline fun <reified T : Any> loadInline(dir: String, fileName: String): T? {
        return load(dir, fileName, serializer())
    }

    inline fun <reified T : Any> loadInline(file: File): T? {
        return load(file, serializer())
    }
}
