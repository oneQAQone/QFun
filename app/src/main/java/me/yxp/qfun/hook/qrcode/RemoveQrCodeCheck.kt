package me.yxp.qfun.hook.qrcode

import com.tencent.open.agent.QrAgentLoginManager
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.replaceFirstParam
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethodOrNull
import java.lang.reflect.Method

@HookItemAnnotation(
    "解除扫码限制",
    "解除长按识别或从相册中扫描二维码时的风险检查",
    HookCategory.OTHER
)
object RemoveQrCodeCheck : BaseSwitchHookItem() {

    private lateinit var check: Method

    override fun onInit(): Boolean {

        val manager = QrAgentLoginManager::class.java

        check = manager.findMethodOrNull {
            returnType = void
            paramTypes(boolean, string, bundle)
        } ?: manager.findMethod {
            returnType = void
            paramTypes(manager, boolean, string, bundle)
        }
        return super.onInit()
    }

    override fun onHook() {
        check.replaceFirstParam(false, this)
    }
}