package me.yxp.qfun.hook.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.EditText
import com.tencent.input.base.mvicompat.DelegateMediator
import com.tencent.mobileqq.aio.input.reply.InputReplyUIState
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.instance
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher
import java.io.File
import java.lang.reflect.Method

@HookItemAnnotation(
    "发送文字表情复合消息",
    "当输入框有任意内容时，点击表情会暂存在输入框中与其他消息混合发送",
    HookCategory.CHAT
)
object MixedEmoAndText : BaseSwitchHookItem(), SendMsgListener, DexKitTask {

    private lateinit var createImageSpan: Method

    private lateinit var handleUIState: Method

    private var isReplying = false

    override fun onSend(elements: ArrayList<MsgElement>) {

        val first = elements.firstOrNull() ?: return
        if ((isReplying || !getInputEdit()?.text.isNullOrEmpty()) && elements.size == 1 && first.elementType == 2) {
            insertImage(first.picElement.sourcePath)
            elements.clear()
        }

    }

    override fun onInit(): Boolean {
        createImageSpan = requireClass("ImageUtils").findMethod {
            paramTypes(string, Uri::class.java, boolean)
        }
        handleUIState = DelegateMediator::class.java.findMethod {
            returnType = void
            paramTypes(obj)
        }
        return super.onInit()
    }

    override fun onHook() {
        handleUIState.hookAfter(this) { param ->
            val state = param.args.firstOrNull()
            isReplying = when (state) {
                is InputReplyUIState.SetReplyUIState -> true
                is InputReplyUIState.ClearReplyUIState -> false
                else -> isReplying
            }
        }
    }

    private fun insertImage(localPath: String) {
        createImageSpan(localPath)?.let {
            insertAtCursor(it)
        }
    }

    private fun insertAtCursor(text: CharSequence) {
        getInputEdit()?.let {
            it.post {
                val start = it.selectionStart
                val end = it.selectionEnd
                it.text.replace(start, end, text)
            }
        }
    }

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

    private fun createImageSpan(localPath: String): SpannableStringBuilder? {

        val file = File(localPath)
        if (!file.exists()) return null

        val customSpan = createImageSpan.invoke(
            requireClass("ImageUtils").instance,
            localPath,
            Uri.fromFile(file),
            false
        )

        return SpannableStringBuilder("图片").apply {
            setSpan(customSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "ImageUtils" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.input.fullscreen")
            matcher {
                usingStrings("sampleSize")
            }
        }
    )

}