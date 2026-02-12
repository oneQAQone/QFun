package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.toClass

@HookItemAnnotation(
    "屏蔽AIO动画特效",
    "屏蔽聊天界面中的关键词彩蛋、表情连击、超级表情等全屏动画",
    HookCategory.PURIFY
)
object BlockAIOEffects : BaseSwitchHookItem() {

    override fun onInit() = HostInfo.isQQ

    override fun onHook() {
        "com.tencent.mobileqq.aio.animation.AIOAnimationContainer".toClass
            .findMethods {
                returnType = boolean
                paramTypes(int, int, objArr)
            }
            .forEach { it.returnConstant(this, true) }
    }
}