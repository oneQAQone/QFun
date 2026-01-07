package me.yxp.qfun.hook.chat

import com.tencent.qqnt.kernel.nativeinterface.MsgAttributeInfo
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.GetMsgRecordListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.qq.QQCurrentEnv

@HookItemAnnotation(
    "默认气泡和字体",
    "在聊天界面中将他人的气泡和字体全部显示为默认",
    HookCategory.CHAT
)
object DefaultBubbleAndFont : BaseSwitchHookItem(), GetMsgRecordListener {

    override fun onGet(msgRecord: MsgRecord) {
        if (msgRecord.senderUin != QQCurrentEnv.currentUin.toLong())
            msgRecord.msgAttrs = HashMap<Int, MsgAttributeInfo>()

    }

}