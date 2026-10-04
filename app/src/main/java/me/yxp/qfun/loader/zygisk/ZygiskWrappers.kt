package me.yxp.qfun.loader.zygisk

import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.loader.hookapi.Invoker
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method

class ZygiskChain(
    override val method: Member,
    private val rawThis: Any?,
    override var args: Array<Any?>,
    private val dispatcher: ZygiskDispatcher,
    private val snapshot: List<ZygiskDispatcher.Node>,
    private val index: Int,
    private val maxPriority: Int
) : Chain {

    override val thisObject: Any
        get() = rawThis ?: throw IllegalStateException(
            "Cannot access 'thisObject' on static method: $method"
        )

    var isReturnEarly: Boolean = false
        private set

    override var result: Any? = null
        set(value) {
            field = value
            isReturnEarly = true
            throwable = null
        }

    override var throwable: Throwable? = null
        set(value) {
            field = value
            if (value != null) {
                isReturnEarly = true
                result = null
            }
        }

    override fun proceed(args: Array<Any?>): Any? {
        this.args = args
        // 沿用当前快照继续向后执行下一层拦截器
        return dispatcher.proceed(snapshot, index + 1, rawThis, args, maxPriority)
    }
}

class ZygiskInvoker(
    private val method: Member,
    private val dispatchers: Map<Member, ZygiskDispatcher>
) : Invoker {

    private val isConstructor = method is Constructor<*>

    init {
        // 在初始化阶段放开访问权限
        (method as AccessibleObject).isAccessible = true
    }

    private fun resolveThis(thisObject: Any?): Any? {
        return if (isConstructor && thisObject == null) {
            val (unsafe, allocateMethod) = unsafeInvoker
            allocateMethod.invoke(unsafe, (method as Constructor<*>).declaringClass)
        } else {
            thisObject
        }
    }

    override fun invokeOrigin(thisObject: Any?, vararg args: Any?): Any? {
        val dispatcher = dispatchers[method]
        return if (dispatcher != null) {
            // 已 Hook：由 LSPlant 的 backupMethod 完成底层调用
            val targetThis = resolveThis(thisObject)
            val result = dispatcher.callBackup(targetThis, arrayOf(*args))
            if (isConstructor && thisObject == null) targetThis else result
        } else {
            // 未 Hook：降级反射执行
            invokeFallback(thisObject, arrayOf(*args))
        }
    }

    override fun invokeWithMaxPriority(maxPriority: Int, thisObject: Any?, vararg args: Any?): Any? {
        val dispatcher = dispatchers[method]
        return if (dispatcher != null) {
            val targetThis = resolveThis(thisObject)
            val snapshot = dispatcher.interceptors
            val result = dispatcher.proceed(snapshot, 0, targetThis, arrayOf(*args), maxPriority)
            if (isConstructor && thisObject == null) targetThis else result
        } else {
            invokeFallback(thisObject, arrayOf(*args))
        }
    }

    /**
     * 仅在目标方法从未被 Hook 时降级反射
     */
    private fun invokeFallback(thisObj: Any?, args: Array<Any?>): Any? {
        return try {
            when (method) {
                is Method -> method.invoke(thisObj, *args)
                is Constructor<*> -> {
                    // 没有 Dispatcher backupMethod 时，无法对已有实例执行 <init> 初始化
                    require(thisObj == null) {
                        "Cannot invoke constructor on an existing instance without hook backup: $method"
                    }
                    method.newInstance(*args)
                }
                else -> throw IllegalArgumentException("Unsupported executable: $method")
            }
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        }
    }

    companion object {
        private val unsafeInvoker: Pair<Any?, Method> by lazy {
            val clazz = Class.forName("sun.misc.Unsafe")
            val theUnsafe = clazz.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null)
            val allocateMethod = clazz.getMethod("allocateInstance", Class::class.java)
            theUnsafe to allocateMethod
        }
    }
}