package me.yxp.qfun.hook.chat

import android.content.Intent
import android.net.Uri
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
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv

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
            setClickableSpan(msgData.atMap, textViewList)
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

    private fun setClickableSpan(atMap: HashMap<String, String>, textViewList: List<TextView>) {
        atMap.forEach { (uin, name) ->
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
                                openUserProfileCard(uin)
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

    private fun openUserProfileCard(uin: String) {
        runCatching {
            val url =
                "mqqapi://userprofile/friend_profile_card?src_type=web&version=1.0&source=2&uin=$uin"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            QQCurrentEnv.activity?.startActivity(intent)
        }.onFailure {
            LogUtils.e(this, it)
        }
    }
}