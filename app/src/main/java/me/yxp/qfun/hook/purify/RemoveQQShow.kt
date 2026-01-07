package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.clazz

@HookItemAnnotation(
    "屏蔽QQ秀",
    "屏蔽新版QQ秀头像",
    HookCategory.PURIFY
)
object RemoveQQShow : BaseSwitchHookItem() {

    override fun onHook() {
        "com.tencent.mobileqq.ai.avatar.api.impl.AIAvatarSwitchApiImpl".clazz
            ?.getDeclaredMethod(
                "isQQShowEnableForAIO",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Long::class.javaPrimitiveType
            )
            ?.hookAfter(this) { param ->
                val uin = param.args[2].toString()
                if (uin != QQCurrentEnv.currentUin) param.result = false
            }
    }
}