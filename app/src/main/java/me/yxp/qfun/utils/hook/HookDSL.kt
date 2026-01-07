package me.yxp.qfun.utils.hook

import me.yxp.qfun.hook.base.BaseHookItem
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodReplacement
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import me.yxp.qfun.utils.log.LogUtils
import java.lang.reflect.Member

class HookHelper(private val owner: BaseHookItem?) {
    private var beforeBlock: ((XC_MethodHook.MethodHookParam) -> Unit)? = null
    private var afterBlock: ((XC_MethodHook.MethodHookParam) -> Unit)? = null
    private var replaceBlock: ((XC_MethodHook.MethodHookParam) -> Any?)? = null

    var priority: Int = 50

    fun before(block: (XC_MethodHook.MethodHookParam) -> Unit) {
        beforeBlock = block
    }

    fun after(block: (XC_MethodHook.MethodHookParam) -> Unit) {
        afterBlock = block
    }

    fun replace(block: (XC_MethodHook.MethodHookParam) -> Any?) {
        replaceBlock = block
    }

    internal fun build(): XC_MethodHook {
        if (replaceBlock != null) {
            return object : XC_MethodReplacement(priority) {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    return try {
                        if (owner?.isEnable == false) {
                            param.invokeOriginal()
                        } else {
                            replaceBlock!!.invoke(param)
                        }
                    } catch (t: Throwable) {
                        LogUtils.e("HookReplaceError: ${owner?.name ?: "Unknown"}", t)
                        param.invokeOriginal()
                    }
                }
            }
        } else {
            return object : XC_MethodHook(priority) {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (owner?.isEnable == false) return
                    runCatching { beforeBlock?.invoke(param) }
                        .onFailure {
                            LogUtils.e(
                                "HookBeforeError: ${owner?.name ?: "Unknown"}",
                                it
                            )
                        }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    if (owner?.isEnable == false) return
                    runCatching { afterBlock?.invoke(param) }
                        .onFailure { LogUtils.e("HookAfterError: ${owner?.name ?: "Unknown"}", it) }
                }
            }
        }
    }
}

fun Member.hook(
    owner: BaseHookItem? = null,
    priority: Int = 50,
    block: HookHelper.() -> Unit
): XC_MethodHook.Unhook {
    val helper = HookHelper(owner).apply {
        this.priority = priority
        block()
    }
    return XposedBridge.hookMethod(this, helper.build())
}