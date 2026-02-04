package me.yxp.qfun.hook.social

import com.tencent.mobileqq.data.Card
import com.tencent.mobileqq.profilecard.activity.FriendProfileCardActivity
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.toClass

@HookItemAnnotation(
    "查看封号资料卡",
    "去除查看封禁账号资料卡的限制",
    HookCategory.SOCIAL
)
object CheckForbidCard : BaseSwitchHookItem() {

    override fun onHook() {
        val activity = if (HostInfo.isTIM)
            "com.tencent.mobileqq.profilecard.activity.TimFriendProfileCardActivity".toClass
        else FriendProfileCardActivity::class.java

        activity.findMethod {
                name = "onCardUpdate"
            }
            .hookBefore(this) { param ->
                val card = param.args[0] as Card
                card.forbidCode = 0
                card.isForbidAccount = false
            }

    }
}