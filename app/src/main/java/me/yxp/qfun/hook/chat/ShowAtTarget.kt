package me.yxp.qfun.hook.chat

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.view.children
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.qqnt.aio.adapter.api.IContactApi
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.api

@HookItemAnnotation(
    "显示艾特对象",
    "点击消息中的艾特部分可直接跳转其主页（此功能可能导致滑动掉帧）",
    HookCategory.CHAT
)
object ShowAtTarget : BaseSwitchHookItem(), AIOViewUpdateListener {

    override fun onUpdate(frameLayout: FrameLayout, msgRecord: MsgRecord) {

        val msgData = MsgData(msgRecord)

        if (msgData.type != 2 || (msgData.msgType != 2 && msgData.msgType != 9) || msgData.atMap.isEmpty()) {
            return
        }

        val textViewList = mutableListOf<TextView>()
        val linearLayout = frameLayout.getChildAt(0) as LinearLayout
        val origin = linearLayout.getChildAt(0)
        findAllTextViews(origin, textViewList)

        if (textViewList.isNotEmpty()) {
            setClickableSpan(msgData, textViewList)
        }
    }

    private fun findAllTextViews(view: View, result: MutableList<TextView>) {
        if (view is TextView) {
            result.add(view)
            return
        }
        if (view is ViewGroup) {
            for (child in view.children) {
                findAllTextViews(child, result)
            }
        }
    }

    private fun setClickableSpan(msgData: MsgData, textViewList: List<TextView>) {

        msgData.atMap.forEach { (uin, name) ->
            val msgRecord = MsgRecord().apply {
                senderUin = uin.toLong()
                peerUin = msgData.peerUin.toLong()
                chatType = 2
            }
            textViewList.forEach { textView ->
                val text = textView.text
                if (text is Spannable) {
                    val startIndex = text.toString().indexOf(name)
                    if (startIndex != -1) {
                        val endIndex = startIndex + name.length

                        text.getSpans<ClickableSpan>(startIndex, endIndex).forEach {
                            text.removeSpan(it)
                        }
                        text.setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                openUserProfileCard(msgRecord)
                            }
                        }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        if (textView.movementMethod !is LinkMovementMethod) {
                            textView.movementMethod = LinkMovementMethod.getInstance()
                        }

                    }
                }
            }
        }
    }

    private fun openUserProfileCard(msgRecord: MsgRecord) {
        QQCurrentEnv.activity?.let {
            api<IContactApi>().openProfileCard(it, AIOMsgItem(msgRecord))
        }
    }
}