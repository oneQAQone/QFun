package me.yxp.qfun.utils.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

enum class Visibility {
    PUBLIC, PROTECTED, PRIVATE, PACKAGE
}

class MethodSearcher(private val clazz: Class<*>) {
    var name: String? = null
    var returnType: Class<*>? = null
    var paramTypes: Array<out Class<*>?>? = null
    var paramCount: Int? = null
    var isStatic: Boolean? = null
    var visibility: Visibility? = null

    val private get() = Visibility.PRIVATE
    val public get() = Visibility.PUBLIC
    val protected get() = Visibility.PROTECTED
    val pkg get() = Visibility.PACKAGE

    val void: Class<*> get() = Void.TYPE
    val boolean: Class<*> get() = java.lang.Boolean.TYPE
    val byte: Class<*> get() = java.lang.Byte.TYPE
    val short: Class<*> get() = java.lang.Short.TYPE
    val int: Class<*> get() = Integer.TYPE
    val long: Class<*> get() = java.lang.Long.TYPE
    val float: Class<*> get() = java.lang.Float.TYPE
    val double: Class<*> get() = java.lang.Double.TYPE
    val char: Class<*> get() = Character.TYPE

    val string: Class<*> get() = String::class.java
    val obj: Class<*> get() = Any::class.java
    val map: Class<*> get() = Map::class.java
    val hashMap: Class<*> get() = HashMap::class.java
    val list: Class<*> get() = List::class.java
    val arrayList: Class<*> get() = ArrayList::class.java
    val set: Class<*> get() = Set::class.java
    val context: Class<*> get() = android.content.Context::class.java
    val bundle: Class<*> get() = android.os.Bundle::class.java
    val view: Class<*> get() = android.view.View::class.java

    val byteArr: Class<*> get() = ByteArray::class.java
    val intArr: Class<*> get() = IntArray::class.java
    val longArr: Class<*> get() = LongArray::class.java
    val floatArr: Class<*> get() = FloatArray::class.java
    val stringArr: Class<*> get() = Array<String>::class.java
    val objArr: Class<*> get() = Array<Any>::class.java

    fun paramTypes(vararg types: Class<*>?) {
        this.paramTypes = types
    }

    internal fun uniqueKey(): String {
        return buildString {
            append(ReflectCache.generateMethodKey(clazz, name, paramTypes))
            append(returnType?.name ?: "*")
            append(paramCount ?: "*")
            isStatic?.let { append(if (it) ":static" else ":instance") }
            visibility?.let { append(":$it") }
        }
    }

    internal fun match(method: Method): Boolean {
        if (name != null && method.name != name) return false
        if (returnType != null && method.returnType != returnType) return false
        if (paramCount != null && method.parameterCount != paramCount) return false

        if (paramTypes != null) {
            if (method.parameterCount != paramTypes!!.size) return false
            val methodParams = method.parameterTypes
            paramTypes!!.forEachIndexed { index, searchType ->
                if (!methodParams[index].isCompatibleWith(searchType)) {
                    return false
                }
            }
        }

        if (isStatic != null && Modifier.isStatic(method.modifiers) != isStatic) return false

        if (visibility != null) {
            val mod = method.modifiers
            val isMatch = when (visibility!!) {
                Visibility.PUBLIC -> Modifier.isPublic(mod)
                Visibility.PROTECTED -> Modifier.isProtected(mod)
                Visibility.PRIVATE -> Modifier.isPrivate(mod)
                Visibility.PACKAGE -> !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(
                    mod
                )
            }
            if (!isMatch) return false
        }
        return true
    }
}

fun Class<*>.findMethodOrNull(block: MethodSearcher.() -> Unit): Method? {
    val searcher = MethodSearcher(this).apply(block)
    return ReflectCache.getMethod(searcher.uniqueKey()) {
        val candidates = declaredMethods.filter { searcher.match(it) }

        if (candidates.isEmpty()) {
            return@getMethod null
        }

        if (candidates.size == 1 || searcher.paramTypes == null) {
            return@getMethod candidates[0].apply { isAccessible = true }
        }

        return@getMethod candidates.minByOrNull { method ->
            val methodParams = method.parameterTypes
            var score = 0
            val searchTypes = searcher.paramTypes!!

            for (i in searchTypes.indices) {
                val searchType = searchTypes[i] ?: continue
                val paramType = methodParams[i]

                if (searchType == paramType) continue

                var depth = 0
                var current: Class<*>? = searchType
                while (current != null && current != paramType) {
                    depth++
                    current = current.superclass
                }

                score += if (current == paramType) depth else 1000
            }
            score
        }?.apply { isAccessible = true }
    }
}

fun Class<*>.findMethod(block: MethodSearcher.() -> Unit): Method {
    return findMethodOrNull(block)
        ?: throw NoSuchMethodException(
            "Method match failed in ${name}. Searcher: ${
                MethodSearcher(
                    this
                ).apply(block).uniqueKey()
            }"
        )
}

fun Class<*>.findMethods(block: MethodSearcher.() -> Unit): List<Method> {
    val searcher = MethodSearcher(this).apply(block)
    return declaredMethods.filter { searcher.match(it) }.onEach { it.isAccessible = true }
}

class FieldSearcher(private val ownerClass: Class<*>) {
    var name: String? = null
    var type: Class<*>? = null
    var isStatic: Boolean? = null
    var visibility: Visibility? = null
    var inParent: Class<*>? = null

    internal val targetClass: Class<*> get() = inParent ?: ownerClass

    internal fun uniqueKey(): String {
        return buildString {
            append(targetClass.name).append("#F")
            append(name ?: "*")
            append(type?.name ?: "*")
            isStatic?.let { append(if (it) ":static" else ":instance") }
            visibility?.let { append(":$it") }
        }
    }
}

fun Class<*>.findFieldOrNull(block: FieldSearcher.() -> Unit): Field? {
    val searcher = FieldSearcher(this).apply(block)
    return ReflectCache.getField(searcher.uniqueKey()) {
        val target = searcher.targetClass
        if (!searcher.name.isNullOrEmpty()) {
            runCatching { target.getDeclaredField(searcher.name!!) }
                .getOrNull()
                ?.takeIf { checkField(it, searcher) }
                ?.apply { isAccessible = true }
        } else {
            target.declaredFields.firstOrNull { checkField(it, searcher) }
                ?.apply { isAccessible = true }
        }
    }
}

fun Class<*>.findField(block: FieldSearcher.() -> Unit): Field {
    return findFieldOrNull(block)
        ?: throw NoSuchFieldException(
            "Field match failed in ${name}. Criteria: ${
                FieldSearcher(this).apply(
                    block
                ).uniqueKey()
            }"
        )
}

private fun checkField(field: Field, searcher: FieldSearcher): Boolean {
    if (searcher.type != null && !searcher.type!!.isCompatibleWith(field.type)) return false
    if (searcher.isStatic != null && Modifier.isStatic(field.modifiers) != searcher.isStatic) return false

    if (searcher.visibility != null) {
        val mod = field.modifiers
        val isMatch = when (searcher.visibility!!) {
            Visibility.PUBLIC -> Modifier.isPublic(mod)
            Visibility.PROTECTED -> Modifier.isProtected(mod)
            Visibility.PRIVATE -> Modifier.isPrivate(mod)
            Visibility.PACKAGE -> !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(
                mod
            )
        }
        if (!isMatch) return false
    }
    return true
}