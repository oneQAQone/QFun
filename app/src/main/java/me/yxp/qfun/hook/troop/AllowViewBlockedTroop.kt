package me.yxp.qfun.hook.troop

import com.tencent.mobileqq.data.troop.TroopInfo
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.qq.HostInfo

@HookItemAnnotation(
    "查看被封禁群聊",
    "解除被封禁群聊无法进入查看资料的限制（仅QQ）",
    HookCategory.GROUP
)
object AllowViewBlockedTroop : BaseSwitchHookItem() {

    override fun onInit() = HostInfo.isQQ

    override fun onHook() {
        TroopInfo::class.java
            .getDeclaredMethod("isUnreadableBlock")
            .returnConstant(this, false)
    }
}