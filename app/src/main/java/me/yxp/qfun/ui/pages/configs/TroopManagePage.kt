package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.TroopManageConfig
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun TroopManagePage(
    currentConfig: TroopManageConfig,
    onSave: (TroopManageConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var doubleTap by remember(currentConfig) { mutableStateOf(currentConfig.doubleTap) }

    ConfigPageScaffold(
        title = "简洁群管",
        configData = TroopManageConfig(doubleTap),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        SelectionGroup {
            SelectionItem(
                title = "单击头像",
                subtitle = "点一下头像立刻弹出群管菜单，QQ 原「点头像看资料」被替换",
                isSelected = !doubleTap,
                onClick = { doubleTap = false }
            )
            SelectionItem(
                title = "双击头像",
                subtitle = "双击头像弹出群管菜单，单击仍会打开资料但会延后约 300ms",
                isSelected = doubleTap,
                onClick = { doubleTap = true }
            )
        }
    }
}
