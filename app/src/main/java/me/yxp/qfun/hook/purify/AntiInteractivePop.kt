package me.yxp.qfun.hook.purify


import com.tencent.mobileqq.springhb.interactive.ui.InteractivePopManager
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.toClass
import java.lang.reflect.Method

@HookItemAnnotation(
    "屏蔽弹出动画",
    "屏蔽某些特定文本消息弹出的与游戏相关的烦人动画",
    HookCategory.PURIFY
)
object AntiInteractivePop : BaseSwitchHookItem() {

    private lateinit var popup: Method

    override fun onInit(): Boolean {
        if (HostInfo.isTIM) return false
        popup = InteractivePopManager::class.java
            .findMethod {
                returnType = void
                paramTypes(
                    "androidx.fragment.app.Fragment".toClass,
                    null,
                    null,
                    "kotlin.jvm.functions.Function0".toClass
                )
            }
        return super.onInit()

    }

    override fun onHook() {
        popup.doNothing(this)
    }
}