package me.yxp.qfun.loader.modern

import io.github.libxposed.api.XposedInterface
import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.loader.hookapi.HookParam
import me.yxp.qfun.loader.hookapi.IHookEngine
import me.yxp.qfun.loader.hookapi.Invoker
import me.yxp.qfun.loader.hookapi.Unhook
import java.lang.reflect.Executable
import java.lang.reflect.Member

class ModernHookEngine(private val base: XposedInterface) : IHookEngine {

    override val apiLevel: Int = base.apiVersion
    override val frameworkName: String = base.frameworkName
    override val frameworkVersion: String = base.frameworkVersion
    override val frameworkVersionCode: Long = base.frameworkVersionCode
    override val bridgeClass: Class<*>? = null

    override fun hookBefore(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook {
        val handle = base.hook(method as Executable).setPriority(priority).intercept { chain ->
            val param = ModernHookParam(chain)
            callback(param)
            if (param.isReturnEarly) return@intercept param.result
            return@intercept chain.proceed(param.args)
        }
        return Unhook { handle.unhook() }
    }

    override fun hookAfter(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook {
        val handle = base.hook(method as Executable).setPriority(priority).intercept { chain ->
            val param = ModernHookParam(chain)
            try {
                param.result = chain.proceed(param.args)
            } catch (t: Throwable) {
                param.throwable = t
            }
            callback(param)
            if (param.throwable != null) throw param.throwable!!
            return@intercept param.result
        }
        return Unhook { handle.unhook() }
    }

    override fun hookReplace(method: Member, priority: Int, callback: (Chain) -> Any?): Unhook {
        val handle = base.hook(method as Executable).setPriority(priority).intercept { chain ->
            val modernChain = ModernChain(chain)
            return@intercept callback(modernChain)
        }
        return Unhook { handle.unhook() }
    }

    override fun getInvoker(method: Member): Invoker {
        return ModernInvoker(base, method)
    }

    override fun deoptimize(method: Member): Boolean {
        return base.deoptimize(method as Executable)
    }

    override fun log(priority: Int, tag: String?, msg: String, t: Throwable?) {
        if (t != null) {
            base.log(priority, tag, msg, t)
        } else {
            base.log(priority, tag, msg)
        }
    }
}