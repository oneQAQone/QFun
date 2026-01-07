package me.yxp.qfun.hook.chat

import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.TimeConfig
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.ShowMsgTimePage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HookItemAnnotation(
    "显示消息时间",
    "显示消息发送时间，点击可设置时间格式及颜色",
    HookCategory.CHAT
)
object ShowMsgTime : BaseClickableHookItem<TimeConfig>(TimeConfig.serializer()),
    AIOViewUpdateListener {

    override val defaultConfig: TimeConfig = TimeConfig()

    private const val VIEW_TAG = "MsgTime"

    override fun onUpdate(frameLayout: FrameLayout, msgRecord: MsgRecord) {
        var timeView = frameLayout.findViewWithTag<TextView>(VIEW_TAG)

        if (timeView == null) {
            timeView = TextView(frameLayout.context).apply {
                tag = VIEW_TAG
                textSize = 10f
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END or Gravity.BOTTOM
                }
            }
            frameLayout.addView(timeView)
        }

        val timeStamp = msgRecord.msgTime * 1000
        timeView.text = formatTime(timeStamp, config.format)
        timeView.setTextColor(config.color)
    }

    fun formatTime(timeStamp: Long, format: String): String {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).format(Date(timeStamp))
        } catch (_: Exception) {
            "Format Error"
        }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        ShowMsgTimePage(
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }
}
