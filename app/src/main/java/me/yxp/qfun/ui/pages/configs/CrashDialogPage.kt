package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.CrashConfig
import me.yxp.qfun.ui.components.listitems.PreferenceSection
import me.yxp.qfun.ui.components.listitems.SwitchItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun CrashDialogPage(
    currentConfig: CrashConfig,
    onSave: (CrashConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var tempConfig by remember(currentConfig) { mutableStateOf(currentConfig) }

    ConfigPageScaffold(
        title = "崩溃提示配置",
        configData = tempConfig,
        onSave = onSave,
        onDismiss = onDismiss
    ) { _ ->
        PreferenceSection(title = "弹窗条件") {
            SwitchItem(
                title = "前台崩溃弹窗",
                description = "在前台崩溃时弹出提示窗口",
                checked = tempConfig.foregroundDialog,
                onCheckedChange = { tempConfig = tempConfig.copy(foregroundDialog = it) }
            )
            SwitchItem(
                title = "后台崩溃静默",
                description = "后台崩溃时不弹窗，仅记录日志",
                checked = tempConfig.backgroundSilent,
                onCheckedChange = { tempConfig = tempConfig.copy(backgroundSilent = it) }
            )
            SwitchItem(
                title = "仅主进程弹窗",
                description = "只有主进程崩溃才弹窗，子进程崩溃静默记录",
                checked = tempConfig.mainProcessOnly,
                onCheckedChange = { tempConfig = tempConfig.copy(mainProcessOnly = it) }
            )
            SwitchItem(
                title = "子线程异常静默",
                description = "只有主线程崩溃才弹窗，子线程异常静默记录",
                checked = tempConfig.childThreadSilent,
                onCheckedChange = { tempConfig = tempConfig.copy(childThreadSilent = it) }
            )
        }
    }
}