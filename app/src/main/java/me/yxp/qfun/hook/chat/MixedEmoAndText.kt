package me.yxp.qfun.hook.chat

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.EditText
import com.tencent.biz.qui.quibutton.QUIButton
import com.tencent.input.base.mvicompat.DelegateMediator
import com.tencent.mobileqq.aio.input.reply.InputReplyUIState
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.msg.api.IMsgUtilApi
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.api
import me.yxp.qfun.utils.reflect.findMethodOrNull

@HookItemAnnotation(
    "发送文字表情复合消息",
    "当输入框有任意内容时，点击表情会暂存在输入框中与其他消息混合发送",
    HookCategory.CHAT
)
object MixedEmoAndText : BaseSwitchHookItem(), SendMsgListener {

    private val emoMap = mutableMapOf<String, MsgElement>()
    private var isReplying = false

    override fun onSend(elements: ArrayList<MsgElement>) {

        val first = elements[0]
        if ((isReplying || !getInputEdit()?.text.isNullOrEmpty()) && elements.size == 1 && first.elementType == 2) {
            val key = "[pic${System.currentTimeMillis()}]"
            insertAtCursor(getInputEdit(), key)
            emoMap[key] = first
            elements.removeAt(0)
        } else if (first.elementType in intArrayOf(1, 6, 7)) {
            val newElements = mutableListOf<MsgElement>()
            elements.forEach {
                if (it.textElement?.atType == 0)
                    newElements.addAll(parseTextToMsgElements(it.textElement.content))
                else newElements.add(it)
            }
            elements.clear()
            elements.addAll(newElements)
        }


    }

    override fun onHook() {
        DelegateMediator::class.java
            .findMethodOrNull {
                returnType = void
                paramTypes(Any::class.java)
            }?.hookAfter(this) { param ->
                val state = param.args.firstOrNull()
                isReplying = when (state) {
                    is InputReplyUIState.SetReplyUIState -> true
                    is InputReplyUIState.ClearReplyUIState -> false
                    else -> isReplying
                }
            }
    }

    @SuppressLint("DiscouragedApi")
    private fun getSendBtn() = runCatching {
        val activity = QQCurrentEnv.activity ?: return@runCatching null
        activity.findViewById<Button>(
            activity.resources.getIdentifier(
                "send_btn",
                "id",
                HostInfo.packageName
            )
        )
    }.getOrNull() as? QUIButton

    @SuppressLint("DiscouragedApi")
    private fun getInputEdit() = runCatching {
        val activity = QQCurrentEnv.activity ?: return@runCatching null
        activity.findViewById<EditText>(
            activity.resources.getIdentifier(
                "input",
                "id",
                HostInfo.packageName
            )
        )
    }.getOrNull()

    private fun insertAtCursor(edit: EditText?, text: String) {
        if (edit == null) return
        val sendBtn = getSendBtn()
        sendBtn?.post {
            sendBtn.setType(1)
            sendBtn.isEnabled = true
        }

        edit.post {
            val start = edit.selectionStart
            val end = edit.selectionEnd
            edit.text.replace(start, end, text)
            edit.setSelection(start + text.length)
        }

    }

    private fun parseTextToMsgElements(text: String): List<MsgElement> {
        val elements = mutableListOf<MsgElement>()
        val regex = "\\[pic\\d{13}]".toRegex()

        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            val range = matchResult.range
            val start = range.first
            val end = range.last + 1

            if (start > lastIndex) {
                val plainText = text.substring(lastIndex, start)
                elements.add(createTextElement(plainText))
            }

            val key = matchResult.value
            elements.add(emoMap[key] ?: createTextElement(key))
            emoMap.remove(key)
            lastIndex = end
        }
        if (lastIndex < text.length) {
            val remainingText = text.substring(lastIndex)
            elements.add(createTextElement(remainingText))
        }

        return elements
    }

    private fun createTextElement(text: String): MsgElement {
        return api<IMsgUtilApi>().createTextElement(text)
    }


}