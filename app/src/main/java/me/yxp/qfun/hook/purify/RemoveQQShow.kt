package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.toClass

@HookItemAnnotation(
    "屏蔽QQ秀",
    "屏蔽新版QQ秀头像",
    HookCategory.PURIFY
)
object RemoveQQShow : BaseSwitchHookItem() {

    override fun onInit() = HostInfo.versionCode >= 11820

    override fun onHook() {

        "com.tencent.mobileqq.ai.avatar.api.impl.AIAvatarSwitchApiImpl".toClass
            .getDeclaredMethod(
                "hasAvatarUrlOrInfo",
                Long::class.java
            )
            .hookAfter(this) {
                if (it.args[0] != QQCurrentEnv.currentUin.toLong()) it.result = false
            }
    }
}