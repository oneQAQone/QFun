package me.yxp.qfun.hook.chat

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    tag = "强制使用系统Emoji",
    desc = "屏蔽QQ自带的Emoji表情，强制显示系统Emoji",
    category = HookCategory.CHAT
)
object SystemEmoji : BaseSwitchHookItem() {
    override fun onHook() {
        val target = "com.tencent.mobileqq.text.EmotcationConstants".clazz ?: return
        target.findMethod { name = "getSingleEmoji" }.returnConstant(this, -1)
        target.findMethod { name = "getDoubleEmoji" }.returnConstant(this, -1)
    }
}