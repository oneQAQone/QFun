package me.yxp.qfun.hook.social

import com.tencent.mobileqq.quibadge.QUIBadge
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter

@HookItemAnnotation(
    "显示具体消息数量",
    "主页聊天界面显示具体消息数量而非99+",
    HookCategory.SOCIAL
)
object ExactMsgCount : BaseSwitchHookItem() {

    override fun onHook() {
        QUIBadge::class.java
            .getDeclaredMethod("updateNum", Int::class.javaPrimitiveType)
            .hookAfter(this) {
                val num = it.args[0].toString()
                QUIBadge::class.java.getDeclaredField("mText").apply {
                    isAccessible = true
                    set(it.thisObject, num)
                }
            }
    }
}