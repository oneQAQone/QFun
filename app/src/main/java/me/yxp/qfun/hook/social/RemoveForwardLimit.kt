package me.yxp.qfun.hook.social

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.setObject
import me.yxp.qfun.utils.reflect.setObjectByType
import me.yxp.qfun.utils.reflect.toClass
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery

@HookItemAnnotation(
    "移除转发数量限制",
    "解除转发联系人时最多选择9人的限制",
    HookCategory.SOCIAL
)
object RemoveForwardLimit : BaseSwitchHookItem(), DexKitTask {

    private lateinit var configClass: Class<*>
    private lateinit var recentClass: Class<*>
    private lateinit var friendClass: Class<*>
    private lateinit var troopClass: Class<*>

    private val isNew: Boolean
        get() = HostInfo.isQQ && HostInfo.versionCode >= 11820

    override fun onInit(): Boolean {

        if (isNew) {
            configClass = requireClass("ChatSelectorConfig")
        } else {
            recentClass = "com.tencent.mobileqq.activity.ForwardRecentActivity".toClass
            friendClass = "com.tencent.mobileqq.activity.ForwardFriendListActivity".toClass
            troopClass = "com.tencent.mobileqq.activity.ForwardTroopListFragment".toClass
        }

        return super.onInit()
    }

    override fun onHook() {
        if (isNew) {

            configClass.declaredConstructors
                .filter { it.parameterCount >= 3 }
                .forEach {
                    it.hookBefore(this) { param ->
                        param.args[2] = Int.MAX_VALUE
                    }
                }
        } else {

            recentClass.declaredConstructors.forEach {
                it.hookAfter(this) { param ->
                    param.thisObject.setObject("mForwardTargetMap", FakeMap())
                }
            }
            friendClass.declaredConstructors.forEach {
                it.hookAfter(this) { param ->
                    param.thisObject.setObjectByType<Map<*, *>>(FakeMap())
                }
            }
            troopClass.declaredConstructors.forEach {
                it.hookAfter(this) { param ->
                    param.thisObject.setObjectByType<Map<*, *>>(FakeMap())
                }
            }
        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "ChatSelectorConfig" to FindClass().apply {
            matcher {
                usingStrings("ChatSelectorConfig")
            }
        }
    )

}

class FakeMap : LinkedHashMap<Any, Any>() {
    override val size: Int
        get() {
            val s = super.size
            return if (s == 9) 8 else s
        }
}