package me.yxp.qfun.utils.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

internal object ReflectCache {

    private val NOT_FOUND = Any()
    private val methodCache = ConcurrentHashMap<String, Any>()
    private val fieldCache = ConcurrentHashMap<String, Any>()
    private val constructorCache = ConcurrentHashMap<String, Any>()

    fun getMethod(key: String, finder: () -> Method?): Method? {
        val result = methodCache.computeIfAbsent(key) {
            finder() ?: NOT_FOUND
        }
        return if (result === NOT_FOUND) null else result as Method
    }

    fun getField(key: String, finder: () -> Field?): Field? {
        val result = fieldCache.computeIfAbsent(key) {
            finder() ?: NOT_FOUND
        }
        return if (result === NOT_FOUND) null else result as Field
    }

    fun getConstructor(key: String, finder: () -> Constructor<*>?): Constructor<*>? {
        val result = constructorCache.computeIfAbsent(key) {
            finder() ?: NOT_FOUND
        }
        return if (result === NOT_FOUND) null else result as Constructor<*>
    }

    fun generateMethodKey(
        clazz: Class<*>,
        methodName: String?,
        paramTypes: Array<out Class<*>?>?
    ): String {
        return buildString {
            append(clazz.name).append("#")
            append(methodName ?: "*").append("(")
            if (paramTypes != null) {
                paramTypes.joinTo(this, ",") { it?.name ?: "?" }
            } else {
                append("*")
            }
            append(")")
        }
    }
}