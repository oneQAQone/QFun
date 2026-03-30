package me.yxp.qfun.loader.modern

import io.github.libxposed.api.XposedInterface
import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.loader.hookapi.HookParam
import me.yxp.qfun.loader.hookapi.Invoker
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

class ModernHookParam(val chain: XposedInterface.Chain) : HookParam {
    
    override val method: Member get() = chain.executable
    
    override val thisObject: Any get() = chain.thisObject
    
    override var args: Array<Any?> = chain.args.toTypedArray()
    
    var isReturnEarly = false
        private set
        
    override var result: Any? = null
    override var throwable: Throwable? = null

    override fun returnEarly(result: Any?) {
        this.result = result
        this.isReturnEarly = true
    }
}

class ModernChain(private val modernParam: ModernHookParam) : Chain, HookParam by modernParam {
    constructor(chain: XposedInterface.Chain) : this(ModernHookParam(chain))

    override fun proceed(args: Array<Any?>): Any? {
        return modernParam.chain.proceed(args)
    }
}

class ModernInvoker(
    private val base: XposedInterface,
    private val method: Member
) : Invoker {

    override fun invokeOrigin(thisObject: Any?, vararg args: Any?): Any? {
        val type = XposedInterface.Invoker.Type.ORIGIN
        return when (method) {
            is Method -> base.getInvoker(method).setType(type).invoke(thisObject, *args)

            is Constructor<*> -> base.getInvoker(method).setType(type).newInstance(*args)

            else -> throw IllegalArgumentException("Unsupported method type")
        }
    }

    override fun invokeWithMaxPriority(maxPriority: Int, thisObject: Any?, vararg args: Any?): Any? {
        val type = XposedInterface.Invoker.Type.Chain(maxPriority)
        return when (method) {
            is Method -> base.getInvoker(method).setType(type).invoke(thisObject, *args)

            is Constructor<*> -> base.getInvoker(method).setType(type).newInstance(*args)

            else -> throw IllegalArgumentException("Unsupported method type")
        }
    }
}