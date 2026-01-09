package me.yxp.qfun.utils.reflect

import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import java.lang.Byte
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.Short
import java.lang.reflect.Member

val Any.TAG: String
    get() = this.javaClass.simpleName

fun Member.callOriginal(obj: Any, vararg args: Any?): Any {
    return XposedBridge.invokeOriginalMethod(this, obj, args)
}

fun Any.callMethod(methodName: String, vararg args: Any?): Any? {
    return this::class.java.findMethodAndCall(this, methodName, args)
}

fun Class<*>.callStaticMethod(methodName: String, vararg args: Any?): Any? {
    return this.findMethodAndCall(null, methodName, args)
}

private fun Class<*>.findMethodAndCall(
    obj: Any?,
    methodName: String,
    args: Array<out Any?>
): Any? {

    val argTypes: Array<Class<*>?> = args.map { it?.javaClass }.toTypedArray()

    val method =
        findMethodOrNull {
            name = methodName
            paramCount = args.size
            paramTypes = argTypes
        }
            ?: throw NoSuchMethodException("Method $methodName not found in $name with args: ${args.map { it?.javaClass?.simpleName ?: "null" }}")

    return try {

        method.invoke(obj, *args)
    } catch (e: Exception) {
        try {
            XposedBridge.invokeOriginalMethod(method, obj, args)
        } catch (_: Exception) {
            throw e
        }
    }
}

fun Any.getObjectByTypeOrNull(type: Class<*>, inParent: Class<*>? = null): Any? {
    return this::class.java.findFieldOrNull {
        this.type = type
        this.inParent = inParent
    }?.get(this)
}

fun Any.getObjectByType(type: Class<*>, inParent: Class<*>? = null): Any {
    return this.getObjectByTypeOrNull(type, inParent)
        ?: throw NoSuchFieldException("Field of type ${type.simpleName} not found in ${this::class.java.name}")
}

inline fun <reified T> Any.getObjectByTypeOrNull(inParent: Class<*>? = null): T? {
    return this.getObjectByTypeOrNull(T::class.java, inParent) as? T
}

inline fun <reified T> Any.getObjectByType(inParent: Class<*>? = null): T {
    return getObjectByTypeOrNull<T>(inParent)
        ?: throw NoSuchFieldException("Field of type ${T::class.java.simpleName} not found in ${this::class.java.name}")
}

fun Any.getObjectOrNull(name: String, inParent: Class<*>? = null): Any? {
    return this::class.java.findFieldOrNull {
        this.name = name
        this.inParent = inParent
    }?.get(this)
}

fun Any.getObject(name: String, inParent: Class<*>? = null): Any {
    return getObjectOrNull(name, inParent)
        ?: throw NoSuchFieldException("Field '$name' not found in ${this::class.java.name}")
}

fun Any.setObject(name: String, value: Any?, inParent: Class<*>? = null) {
    this::class.java.findField {
        this.name = name
        this.inParent = inParent
    }.set(this, value)
}

inline fun <reified T> Any.setObjectByType(value: T?, inParent: Class<*>? = null) {
    this::class.java.findField {
        this.type = T::class.java
        this.inParent = inParent
    }.set(this, value)
}

fun Class<*>.getStaticObject(name: String): Any? {
    return this.findField {
        this.name = name
        this.isStatic = true
    }.get(null)
}

fun Class<*>.newInstanceWithArgs(vararg args: Any?): Any {
    val argTypes: Array<Class<*>?> = args.map { it?.javaClass }.toTypedArray()
    val cacheKey = ReflectCache.generateMethodKey(this, "<init>", argTypes)

    val constructor = ReflectCache.getConstructor(cacheKey) {
        this.declaredConstructors.firstOrNull { ctor ->

            val params = ctor.parameterTypes
            params.size == argTypes.size && params.zip(argTypes).all { (paramType, argType) ->
                paramType.isCompatibleWith(argType)
            }
        }?.apply { isAccessible = true }
    }
        ?: throw NoSuchMethodException("No constructor found for ${this.name} with args: ${args.map { it?.javaClass?.simpleName ?: "null" }}")

    return try {
        constructor.newInstance(*args)
    } catch (e: Exception) {
        throw e
    }
}

private val primitiveWrapperMap = mapOf(
    Integer.TYPE to Integer::class.java,
    Long.TYPE to Long::class.java,
    java.lang.Boolean.TYPE to java.lang.Boolean::class.java,
    Double.TYPE to Double::class.java,
    Float.TYPE to Float::class.java,
    Short.TYPE to Short::class.java,
    Byte.TYPE to Byte::class.java,
    Character.TYPE to Character::class.java
)

fun Class<*>.isCompatibleWith(actualType: Class<*>?): Boolean {

    if (actualType == null) {
        return !this.isPrimitive
    }

    if (this.isAssignableFrom(actualType)) return true

    return if (this.isPrimitive) primitiveWrapperMap[this] == actualType
    else false

}