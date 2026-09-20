package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.RememberLastReadConfig
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun RememberLastReadPage(
    currentConfig: RememberLastReadConfig,
    onSave: (RememberLastReadConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var manualJump by remember(currentConfig) { mutableStateOf(currentConfig.manualJump) }

    ConfigPageScaffold(
        title = "记住上次查看位置",
        configData = RememberLastReadConfig(manualJump),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        SelectionGroup {
            SelectionItem(
                title = "自动跳转",
                subtitle = "进入群聊后自动跳到上次查看的位置",
                isSelected = !manualJump,
                onClick = { manualJump = false }
            )
            SelectionItem(
                title = "点击跳转",
                subtitle = "进入群聊后出现「回到上次位置」按钮，点击才跳转",
                isSelected = manualJump,
                onClick = { manualJump = true }
            )
        }
    }
}
