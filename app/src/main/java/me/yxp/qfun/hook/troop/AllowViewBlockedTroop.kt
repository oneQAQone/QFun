package me.yxp.qfun.hook.troop

import com.tencent.mobileqq.data.troop.TroopInfo
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant

@HookItemAnnotation(
    "查看被封禁群聊",
    "解除被封禁群聊无法进入查看资料的限制",
    HookCategory.GROUP
)
object AllowViewBlockedTroop : BaseSwitchHookItem() {
    override fun onHook() {
        TroopInfo::class.java
            .getDeclaredMethod("isUnreadableBlock")
            .returnConstant(this, false)
    }
}