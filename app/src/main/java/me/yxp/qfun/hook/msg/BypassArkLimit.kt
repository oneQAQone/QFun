package me.yxp.qfun.hook.msg

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "绕过TIM卡片点击限制",
    "解除TIM中部分卡片无法点击的问题",
    HookCategory.MSG
)
object BypassArkLimit : BaseSwitchHookItem(), DexKitTask {

    private lateinit var checkMethod: Method

    override fun onInit(): Boolean {
        
        if (!HostInfo.isTIM) return false

        checkMethod = requireClass("ArkConfigModel").findMethod {
            returnType = boolean
            paramTypes(string, string)
        }
        return super.onInit()
    }

    override fun onHook() {
        checkMethod.returnConstant(this, true)
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "ArkConfigModel" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.ark")
            matcher {
                usingStrings("updateTimArkClickWhiteList")
            }
        }
    )
}