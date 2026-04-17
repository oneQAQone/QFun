package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.toClass

@HookItemAnnotation(
    "屏蔽弹出动画",
    "屏蔽某些特定文本消息弹出的与游戏相关的烦人动画",
    HookCategory.PURIFY
)
object AntiInteractivePop : BaseSwitchHookItem() {

    override fun onInit() = HostInfo.isQQ

    override fun onHook() {
        listOf(
            "com.tencent.mobileqq.springhb.interactive.ui.InteractivePopManager",
            "com.tencent.mobileqq.aio.animation.pag.PagEasterEggPopManager"
        ).forEach {
            it.clazz?.findMethod {
                returnType = void
                paramTypes(
                    "androidx.fragment.app.Fragment".toClass,
                    null,
                    null,
                    "kotlin.jvm.functions.Function0".toClass
                )
            }?.doNothing(this)
        }
    }
}