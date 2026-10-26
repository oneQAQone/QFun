package me.yxp.qfun.hook.other

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.CrashConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.CrashDialogPage

@HookItemAnnotation(
    "崩溃提示",
    "崩溃时弹出提示窗口，点击可细调弹窗条件",
    HookCategory.OTHER
)
object CrashDialog : BaseClickableHookItem<CrashConfig>(CrashConfig.serializer(), defaultEnable = true) {

    override val defaultConfig: CrashConfig = CrashConfig()

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        CrashDialogPage(
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }
}