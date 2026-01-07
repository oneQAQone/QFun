package me.yxp.qfun.hook.base

import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.utils.qq.HostInfo

abstract class BaseHookItem {

    val name: String = this::class.java.simpleName

    open var isEnable = true

    protected val annotation: HookItemAnnotation? by lazy {
        this::class.java.getAnnotation(HookItemAnnotation::class.java)
    }

    fun isInTargetProcess(): Boolean {
        val target = annotation?.process ?: return false
        if (target == "All") return true
        val currentProcess = HostInfo.processName
        return currentProcess == "${HostInfo.packageName}$target"
    }


}