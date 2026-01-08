package me.yxp.qfun.hook.troop

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.TroopSetConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.TroopSelectorPage
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.scheduler.PrecisionScheduler
import me.yxp.qfun.utils.scheduler.ScheduledTask

@HookItemAnnotation(
    "群打卡",
    "点击选择群聊",
    HookCategory.GROUP
)
object AutoGroupClockIn : BaseClickableHookItem<TroopSetConfig>(TroopSetConfig.serializer()) {

    override val defaultConfig: TroopSetConfig = TroopSetConfig()

    private fun scheduleClockIn() {
        val targetMidnight = PrecisionScheduler.getNextMidnight()

        val preciseTask = ScheduledTask(
            "DailyClockIn_Exact",
            targetMidnight
        ) {
            if (!isEnable) return@ScheduledTask
            doClockIn()
        }

        val backupTask = ScheduledTask(
            "DailyClockIn_Backup",
            targetMidnight + 500
        ) {
            if (!isEnable) return@ScheduledTask
            doClockIn()
            scheduleClockIn()
        }

        PrecisionScheduler.addTask(preciseTask)
        PrecisionScheduler.addTask(backupTask)
    }

    private fun doClockIn() {
        config.selectedSet.forEach {
            ModuleScope.launchIO("ClockIn") {
                TroopTool.clockIn(it)
            }
        }
    }

    override fun onHook() {
        PrecisionScheduler.init()
        scheduleClockIn()
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        TroopSelectorPage(
            title = "选择你要打卡的群聊",
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }
}
