package me.yxp.qfun.loader.zygisk

import android.util.Log
import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.loader.hookapi.HookParam
import me.yxp.qfun.loader.hookapi.IHookEngine
import me.yxp.qfun.loader.hookapi.Invoker
import me.yxp.qfun.loader.hookapi.Unhook
import java.lang.reflect.Member
import java.util.concurrent.ConcurrentHashMap

class ZygiskHookEngine : IHookEngine {

    override val apiLevel: Int = 1
    override val frameworkName: String = "QFun-Zygisk"
    override val frameworkVersion: String = "1"
    override val frameworkVersionCode: Long = 1
    override val bridgeClass: Class<*>? = null

    private val dispatchers = ConcurrentHashMap<Member, ZygiskDispatcher>()

    private fun getOrCreateDispatcher(method: Member): ZygiskDispatcher {
        return dispatchers.computeIfAbsent(method) { target ->
            val dispatcher = ZygiskDispatcher(target)
            val backup = ZygiskBridge.hookMethod(
                target,
                dispatcher,
                ZygiskDispatcher.CALLBACK_METHOD
            )?.apply { isAccessible = true }
                ?: throw IllegalStateException("LSPlant failed to hook member: $target")

            dispatcher.backupMethod = backup
            dispatcher
        }
    }

    private fun registerInternal(
        method: Member,
        priority: Int,
        interceptor: (ZygiskChain) -> Any?
    ): Unhook {
        return getOrCreateDispatcher(method).register(priority, interceptor)
    }

    override fun hookBefore(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook =
        registerInternal(method, priority) { chain ->
            callback(chain)
            if (chain.isReturnEarly) {
                chain.throwable?.let { throw it }
                chain.result
            } else {
                chain.proceed(chain.args)
            }
        }

    override fun hookAfter(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook =
        registerInternal(method, priority) { chain ->
            try {
                chain.result = chain.proceed(chain.args)
            } catch (t: Throwable) {
                chain.throwable = t
            }
            callback(chain)
            chain.throwable?.let { throw it }
            chain.result
        }

    override fun hookReplace(method: Member, priority: Int, callback: (Chain) -> Any?): Unhook =
        registerInternal(method, priority, callback)

    override fun getInvoker(method: Member): Invoker {
        return ZygiskInvoker(method, dispatchers)
    }

    override fun deoptimize(method: Member): Boolean {
        return ZygiskBridge.deoptimize(method)
    }

    override fun log(priority: Int, tag: String?, msg: String, t: Throwable?) {
        val logTag = tag ?: "QFun-Zygisk"
        val logContent = if (t != null) "$msg\n${Log.getStackTraceString(t)}" else msg
        Log.println(priority, logTag, logContent)
    }
}