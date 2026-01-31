package me.yxp.qfun.hook.social

import com.tencent.mobileqq.data.Card
import com.tencent.mobileqq.profilecard.activity.FriendProfileCardActivity
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    "查看封号资料卡",
    "去除查看封禁账号资料卡的限制",
    HookCategory.SOCIAL
)
object CheckForbidCard : BaseSwitchHookItem() {

    override fun onHook() {
        FriendProfileCardActivity::class.java
            .findMethod {
                name = "onCardUpdate"
            }
            .hookBefore(this) { param ->
                val card = param.args[0] as Card
                card.forbidCode = 0
                card.isForbidAccount = false
            }

    }
}