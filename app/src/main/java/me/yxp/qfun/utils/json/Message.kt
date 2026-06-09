package me.yxp.qfun.utils.json

import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.reflect.findMethod
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Modifier

object MessageTool : DexKitTask {

    private val getValueMap by lazy {
        requireClass("Message").findMethod {
            returnType = map
            paramCount = 0
        }
    }

    private val putValue by lazy {
        requireClass("Message").findMethod {
            returnType = void
            paramTypes(int, obj)
        }
    }

    private val readValue by lazy {
        requireClass("FieldDescriptor").findMethod {
            returnType = obj
            paramCount = 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getValue(message: Any, tag: Int): Any? {
        val valueMap = getValueMap.invoke(message) as Map<Int, Any?>
        return valueMap[tag]?.readValue()
    }

    fun putValue(message: Any, tag: Int, value: Any?) {
        putValue.invoke(message, tag, value)
    }

    private fun Any.readValue(): Any? {
        return readValue.invoke(this)
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "Message" to FindClass().apply {
            searchPackages("pbandk")
            matcher {
                usingStrings("Not yet implemented")
            }
        },

        "FieldDescriptor" to FindClass().apply {
            searchPackages("pbandk")
            excludePackages("pbandk.internal")
            matcher {
                fields {
                    count(3)
                    add {
                        type("int")
                        modifiers = Modifier.FINAL
                    }
                    add {
                        type("java.lang.Object")
                    }
                }
            }
        }
    )
}

fun Any.getProtoValue(vararg tags: Int): Any? {
    return tags.fold(this as Any?) { current, tag ->
        current?.let { MessageTool.getValue(it, tag) }
    }
}

inline fun <reified T> Any.getProtoValueAs(vararg tags: Int): T? {
    return getProtoValue(*tags) as? T
}

fun Any.setProtoValue(vararg tags: Int, value: Any?) {
    require(tags.isNotEmpty()) { "Tag path must not be empty" }
    val parent = tags.dropLast(1).fold(this as Any?) { current, tag ->
        current?.let { MessageTool.getValue(it, tag) }
    } ?: return
    MessageTool.putValue(parent, tags.last(), value)
}

inline fun Any.replaceProtoValue(vararg tags: Int, transformer: (Any?) -> Any?) {
    val oldValue = getProtoValue(*tags)
    val newValue = transformer(oldValue)
    setProtoValue(*tags, value = newValue)
}

val Class<*>.isMessage: Boolean
    get() = runCatching {
        MessageTool.requireClass("Message").isAssignableFrom(this)
    }.getOrDefault(false)

val Any?.isMessageInstance: Boolean
    get() = this?.javaClass?.isMessage == true