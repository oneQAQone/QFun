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
        return if (method is Method) {
            val invoker = base.getInvoker(method)
            invoker.setType(XposedInterface.Invoker.Type.ORIGIN).invoke(thisObject, *args)
        } else {
            val invoker = base.getInvoker(method as Constructor<*>)
            invoker.setType(XposedInterface.Invoker.Type.ORIGIN).invoke(thisObject, *args)
        }
    }

    override fun invokeWithMaxPriority(maxPriority: Int, thisObject: Any?, vararg args: Any?): Any? {
        return if (method is Method) {
            val invoker = base.getInvoker(method)
            invoker.setType(XposedInterface.Invoker.Type.Chain(maxPriority)).invoke(thisObject, *args)
        } else {
            val invoker = base.getInvoker(method as Constructor<*>)
            invoker.setType(XposedInterface.Invoker.Type.Chain(maxPriority)).invoke(thisObject, *args)
        }
    }
}