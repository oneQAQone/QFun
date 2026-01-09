package me.yxp.qfun.hook.api

import com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation("监听发送消息")
object OnSendMsg : BaseApiHookItem<SendMsgListener>() {


    @Suppress("UNCHECKED_CAST")
    override fun loadHook() {
        IKernelMsgService.CppProxy::class.java
            .findMethod {
                name = "sendMsg"
            }.hookBefore(this) { param ->

                val elements = param.args[2] as ArrayList<MsgElement>
                forEachChecked { it.onSend(elements) }
            }
    }

}

fun interface SendMsgListener : Listener {
    fun onSend(elements: ArrayList<MsgElement>)
}