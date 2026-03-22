package me.yxp.qfun.utils.hook

import me.yxp.qfun.hook.base.BaseHookItem
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.reflect.isCompatibleWith
import java.lang.reflect.Member
import java.lang.reflect.Method

import me.yxp.qfun.loader.hookapi.HookParam
import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.loader.hookapi.Unhook
import me.yxp.qfun.loader.hookapi.HookEngineManager

fun Member.hookBefore(
    owner: BaseHookItem? = null,
    priority: Int = 50,
    block: (HookParam) -> Unit
): Unhook {
    return HookEngineManager.engine.hookBefore(this, priority) { param ->
        if (owner?.isEnable == false) return@hookBefore
        runCatching { block(param) }.onFailure { LogUtils.e("HookBeforeError: ${owner?.name ?: "Unknown"}", it) }
    }
}

fun Member.hookAfter(
    owner: BaseHookItem? = null,
    priority: Int = 50,
    block: (HookParam) -> Unit
): Unhook {
    return HookEngineManager.engine.hookAfter(this, priority) { param ->
        if (owner?.isEnable == false) return@hookAfter
        runCatching { block(param) }.onFailure { LogUtils.e("HookAfterError: ${owner?.name ?: "Unknown"}", it) }
    }
}

fun Member.hookReplace(
    owner: BaseHookItem? = null,
    priority: Int = 50,
    block: (Chain) -> Any?
): Unhook {
    return HookEngineManager.engine.hookReplace(this, priority) { chain ->
        try {
            if (owner?.isEnable == false) {
                chain.proceed()
            } else {
                block(chain)
            }
        } catch (t: Throwable) {
            LogUtils.e("HookReplaceError: ${owner?.name ?: "Unknown"}", t)
            chain.proceed() 
        }
    }
}

fun Chain.invokeOriginal(args: Array<Any?> = arrayOf()): Any? {
    val engine = HookEngineManager.engine
    val method = this.method
    val thisObject = this.thisObject
    return if (args.isNotEmpty()) engine.getInvoker(this.method).invokeOrigin(thisObject, *args)
    else engine.getInvoker(method).invokeOrigin(thisObject, *this.args)
}

fun Member.returnConstant(owner: BaseHookItem? = null, constant: Any?): Unhook {
    return this.hookReplace(owner) { constant }
}

fun Member.doNothing(owner: BaseHookItem?): Unhook {
    return this.hookReplace(owner) { null }
}

inline fun <reified T> HookParam.getFirstArg(): T? {
    return args.find { it is T } as? T
}

inline fun <reified T> HookParam.getArgByType(): T? {
    val methodObj = this.method as? Method ?: return null
    val index = methodObj.parameterTypes.indexOfFirst { T::class.java.isAssignableFrom(it) }
    if (index != -1 && index < args.size) {
        return args[index] as? T
    }
    return null
}

inline fun <reified T> Member.replaceFirstParam(
    newValue: T,
    owner: BaseHookItem? = null
): Unhook {
    return this.hookBefore(owner) { param ->
        val method = param.method as? Method ?: return@hookBefore
        val index = method.parameterTypes.indexOfFirst { it.isCompatibleWith(T::class.java) }
        if (index != -1 && index < param.args.size) {
            param.args[index] = newValue
        }
    }
}

fun Member.replaceParam(
    index: Int,
    value: Any?,
    owner: BaseHookItem? = null
): Unhook {
    return this.hookBefore(owner) { param ->
        if (index in param.args.indices) {
            param.args[index] = value
        }
    }
}