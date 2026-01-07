package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import me.yxp.qfun.conf.TroopSetConfig
import me.yxp.qfun.ui.components.biz.AsyncSelectorDialog
import me.yxp.qfun.ui.components.dialogs.SelectionItem
import me.yxp.qfun.utils.qq.TroopTool

@Composable
fun TroopSelectorPage(
    title: String,
    currentConfig: TroopSetConfig,
    onSave: (TroopSetConfig) -> Unit,
    onDismiss: () -> Unit
) {
    AsyncSelectorDialog(
        title = title,
        currentSelection = currentConfig.selectedSet,
        dataLoader = TroopTool::getGroupList,
        mapper = { SelectionItem(it.group, it.groupName) },
        onDismiss = onDismiss,
        onConfirm = {
            onSave(TroopSetConfig(it))
            onDismiss()
        }
    )
}
