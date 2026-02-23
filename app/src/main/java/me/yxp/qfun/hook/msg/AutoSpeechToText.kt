package me.yxp.qfun.hook.msg

import com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    "语音消息自动转文本",
    "收到语音消息时自动转换为文字显示",
    HookCategory.MSG
)
object AutoSpeechToText : BaseSwitchHookItem() {

    override fun onInit() = HostInfo.isQQ

    override fun onHook() {
        AIOPttContentComponent::class.java.findMethod {
            returnType = boolean
            paramTypes(boolean)
        }.returnConstant(this, true)
    }
}