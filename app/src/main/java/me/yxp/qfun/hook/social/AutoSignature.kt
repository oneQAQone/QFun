package me.yxp.qfun.hook.social

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.SignatureConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.AutoSignaturePage
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.json.findFirstValueByKey
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.net.HttpUtils
import me.yxp.qfun.utils.qq.SignatureTool
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.scheduler.PrecisionScheduler
import me.yxp.qfun.utils.scheduler.ScheduledTask
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar
import kotlin.random.Random

@HookItemAnnotation(
    "定时修改签名",
    "支持本地 TXT 和网络 API ",
    HookCategory.SOCIAL
)
object AutoSignature : BaseClickableHookItem<SignatureConfig>(SignatureConfig.serializer()) {

    override val defaultConfig: SignatureConfig = SignatureConfig()

    private const val TASK_ID = "AutoSignature_Task"

    private fun getNextTargetTime(): Long {
        val now = PrecisionScheduler.currentServerTime()
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

    private fun scheduleTask() {
        val targetTime = getNextTargetTime()
        val preciseTask = ScheduledTask(
            TASK_ID,
            targetTime
        ) {
            if (!isEnable) return@ScheduledTask
            updateSignature()
            scheduleTask()
        }

        PrecisionScheduler.addTask(preciseTask)
    }

    private fun updateSignature() {
        ModuleScope.launchIO(name) {
            val signature = fetchSignature()
            if (signature.isNotEmpty()) {
                SignatureTool.setSignature(signature)
                if (config.notifyOnUpdate) {
                    Toasts.qqToast(2, config.notifyText)
                }
            }
        }
    }

    private suspend fun fetchSignature(): String {
        return try {
            if (config.mode == 0) {
                if (config.jsonPath.isEmpty()) return ""
                val file = File(config.jsonPath)
                if (!file.exists()) return ""
                val content = FileUtils.readText(file) ?: return ""
                val lines = content.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (lines.isNotEmpty()) {
                    val index = Random.nextInt(lines.size)
                    lines[index]
                } else ""
            } else {
                if (config.apiUrl.isEmpty()) return ""
                val response = HttpUtils.getSuspend(config.apiUrl)
                if (config.jsonKey.isNotEmpty()) {
                    val json: Any = if (response.startsWith("[")) JSONArray(response) else JSONObject(response)
                    json.findFirstValueByKey(config.jsonKey)?.toString() ?: ""
                } else {
                    response
                }
            }
        } catch (e: Exception) {
            LogUtils.e(this, e)
            ""
        }
    }

    override fun onHook() {
        PrecisionScheduler.init()
        scheduleTask()
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        AutoSignaturePage(
            currentConfig = config,
            onSave = {
                updateConfig(it)
                scheduleTask()
            },
            onDismiss = onDismiss
        )
    }
}