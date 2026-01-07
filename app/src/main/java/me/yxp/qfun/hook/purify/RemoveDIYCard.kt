package me.yxp.qfun.hook.purify

import com.tencent.mobileqq.profilecard.activity.FriendProfileCardActivity
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.qq.HostInfo

@HookItemAnnotation(
    "屏蔽DIY名片",
    "屏蔽查看好友页面自定义的名片",
    HookCategory.PURIFY
)
object RemoveDIYCard : BaseSwitchHookItem() {

    override fun onInit(): Boolean {
        return HostInfo.isQQ
    }

    override fun onHook() {
        FriendProfileCardActivity::class.java
            .getDeclaredMethod("handleSwitchVasCard")
            .doNothing(this)
    }

}