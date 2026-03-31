package me.yxp.qfun.hook.device

import android.app.Activity
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    "伪装处于非多窗口模式",
    "解除分屏状态下扫码等功能的使用限制",
    HookCategory.DEVICE,
    "All"
)
object DisableSplitScreenLimit : BaseSwitchHookItem() {

    override fun onHook() {
        Activity::class.java
            .findMethod {
                name = "isInMultiWindowMode"
            }
            .returnConstant(this, false)
    }
}