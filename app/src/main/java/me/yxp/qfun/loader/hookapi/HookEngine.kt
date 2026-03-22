package me.yxp.qfun.loader.hookapi

import java.lang.reflect.Member

fun interface Unhook {
    fun unhook()
}

interface Invoker {
    fun invokeOrigin(thisObject: Any?, vararg args: Any?): Any?
    fun invokeWithMaxPriority(maxPriority: Int, thisObject: Any?, vararg args: Any?): Any?
}

interface HookParam {
    val method: Member
    
    val thisObject: Any 
    
    var args: Array<Any?>
    var result: Any?
    var throwable: Throwable?
    
    fun returnEarly(result: Any?)
}

interface Chain : HookParam {
    fun proceed(args: Array<Any?> = this.args): Any?
}

interface IHookEngine {
    val apiLevel: Int
    val frameworkName: String
    val frameworkVersion: String
    val frameworkVersionCode: Long
    val bridgeClass: Class<*>?

    fun hookBefore(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook
    fun hookAfter(method: Member, priority: Int, callback: (HookParam) -> Unit): Unhook
    fun hookReplace(method: Member, priority: Int, callback: (Chain) -> Any?): Unhook
    
    fun getInvoker(method: Member): Invoker
    fun deoptimize(method: Member): Boolean
    
    fun log(priority: Int, tag: String?, msg: String, t: Throwable? = null)
}

object HookEngineManager {
    lateinit var engine: IHookEngine
    
    val isInitialized: Boolean
        get() = this::engine.isInitialized
}