package me.yxp.qfun.hook.api

import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter

@HookItemAnnotation("监听获取消息记录")
object OnGetMsgRecord : BaseApiHookItem<GetMsgRecordListener>() {

    override fun loadHook() {
        AIOMsgItem::class.java.getDeclaredMethod("getMsgRecord")
            .hookAfter(this) { param ->
                val msgRecord = param.result as MsgRecord
                listenerSet.filter { verify(it) }
                    .forEach { it.onGet(msgRecord) }
            }
    }
}

fun interface GetMsgRecordListener : Listener {
    fun onGet(msgRecord: MsgRecord)
}