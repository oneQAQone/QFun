package me.yxp.qfun.hook.social

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.SparkConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.AutoKeepSparkPage
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.scheduler.PrecisionScheduler
import me.yxp.qfun.utils.scheduler.ScheduledTask
import java.util.Calendar

@HookItemAnnotation(
    "自动续火",
    "点击选择群聊和好友，支持脚本图文复合消息，可自定义具体执行时间",
    HookCategory.SOCIAL
)
object AutoKeepSpark : BaseClickableHookItem<SparkConfig>(SparkConfig.serializer()) {

    override val defaultConfig: SparkConfig = SparkConfig()

    private const val TASK_ID = "AutoKeepSpark_Task"

    private fun getNextTargetTime(): Long {
        val now = System.currentTimeMillis()
        val c = Calendar.getInstance()
        c.timeInMillis = now
        c.set(Calendar.HOUR_OF_DAY, config.hour)
        c.set(Calendar.MINUTE, config.minute)
        c.set(Calendar.SECOND, config.second)
        c.set(Calendar.MILLISECOND, 0)

        if (c.timeInMillis <= now) {
            c.add(Calendar.DAY_OF_YEAR, 1)
        }
        return c.timeInMillis
    }

    fun scheduleSend() {
        val targetTime = getNextTargetTime()
        val task = ScheduledTask(
            TASK_ID,
            targetTime
        ) {
            if (!isEnable) return@ScheduledTask
            sendMsg()
            scheduleSend()
        }
        PrecisionScheduler.addTask(task)
    }

    private fun sendMsg() {
        config.contacts.forEach { contactStr ->
            ModuleScope.launchIO("KeepSpark") {
                val uin = contactStr.dropLast(4)
                val chatType = if (contactStr.endsWith("(好友)")) 1 else 2
                MsgTool.sendMsg(uin, config.message, chatType)
            }
        }
    }

    override fun onHook() {
        PrecisionScheduler.init()
        scheduleSend()
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        AutoKeepSparkPage(
            currentConfig = config,
            onSave = {
                updateConfig(it)
                scheduleSend()
            },
            onDismiss = onDismiss
        )
    }
}
