package me.yxp.qfun.utils.hook

import me.yxp.qfun.hook.base.BaseHookItem
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import me.yxp.qfun.utils.reflect.isCompatibleWith
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

fun Member.returnConstant(owner: BaseHookItem? = null, constant: Any?) {
    this.hookReplace(owner) { constant }
}

fun Member.doNothing(owner: BaseHookItem?) {
    this.hookReplace(owner) { null }
}

fun Member.printStack(tag: String = "HookStack"): XC_MethodHook.Unhook {
    return this.hook {
        before {
            XposedBridge.log("[$tag] Method ${it.method.name} called")
            XposedBridge.log(Throwable("Stack trace"))
        }
    }
}

inline fun <reified T> Member.replaceFirstParam(
    newValue: T,
    owner: BaseHookItem? = null
): XC_MethodHook.Unhook {
    return this.hook(owner) {
        before { param ->

            val args = param.args

            if (args != null) {

                val index = (param.method as Method).parameterTypes.indexOfFirst {
                    it.isCompatibleWith(T::class.java)
                }

                if (index != -1 && index < args.size) {
                    args[index] = newValue
                }
            }
        }
    }
}

fun Member.replaceParam(
    index: Int,
    value: Any?,
    owner: BaseHookItem? = null
): XC_MethodHook.Unhook {
    return this.hook(owner) {
        before { param ->
            if (param.args != null && index >= 0 && index < param.args.size) {
                param.args[index] = value
            }
        }
    }
}

fun Member.hookBefore(
    owner: BaseHookItem? = null,
    block: (XC_MethodHook.MethodHookParam) -> Unit
): XC_MethodHook.Unhook {
    return this.hook(owner) {
        before(block)
    }
}

fun Member.hookAfter(
    owner: BaseHookItem? = null,
    block: (XC_MethodHook.MethodHookParam) -> Unit
): XC_MethodHook.Unhook {
    return this.hook(owner) {
        after(block)
    }
}

fun Member.hookReplace(
    owner: BaseHookItem? = null,
    block: (XC_MethodHook.MethodHookParam) -> Any?
): XC_MethodHook.Unhook {
    return this.hook(owner) {
        replace(block)
    }
}

val XC_MethodHook.MethodHookParam.parameterTypes: Array<Class<*>>
    get() = (this.method as? Method)?.parameterTypes
        ?: (this.method as? Constructor<*>)?.parameterTypes
        ?: emptyArray()

fun XC_MethodHook.MethodHookParam.invokeOriginal(args: Array<Any?> = this.args): Any? {
    return XposedBridge.invokeOriginalMethod(
        this.method,
        this.thisObject,
        args
    )
}

inline fun <reified T> XC_MethodHook.MethodHookParam.getFirstArg(): T? {
    return args?.find { it is T } as? T
}

inline fun <reified T> XC_MethodHook.MethodHookParam.getArgByType(): T? {
    val index = parameterTypes.indexOfFirst { T::class.java.isAssignableFrom(it) }
    if (index != -1 && args != null && index < args.size) {
        return args[index] as? T
    }
    return null
}