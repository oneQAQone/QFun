package me.yxp.qfun.hook.qrcode

import android.os.Bundle
import com.tencent.biz.qrcode.activity.QRLoginAuthActivity
import com.tencent.biz.qui.quibutton.QUIButton
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.getObjectByType

@HookItemAnnotation(
    "跳过扫码确认等待时间",
    "可忽略倒计时，直接点击确认即可",
    HookCategory.OTHER
)
object SkipScanWaitTime : BaseSwitchHookItem() {

    override fun onHook() {

        QRLoginAuthActivity::class.java
            .getDeclaredMethod("doOnCreate", Bundle::class.java)
            .hookAfter(this) { param ->
                val activity = param.thisObject
                val confirmButton = activity.getObjectByType<QUIButton>()

                ModuleScope.launchDelayed(100) {
                    confirmButton.isEnabled = true
                    confirmButton.setType(0)
                }

            }
    }
}