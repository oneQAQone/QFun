package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.ShortcutConfig
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.utils.qq.HostInfo

@Composable
fun ShortcutBarPage(
    currentConfig: ShortcutConfig,
    onSave: (ShortcutConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var tempIds by remember(currentConfig) { mutableStateOf(currentConfig.visibleIds) }
    val hasBubblesSlot = HostInfo.versionCode >= 11310

    val options = remember {
        buildList {
            add(Triple(1000, "语音", "语音消息按钮"))
            add(Triple(1001, "表情", "表情面板按钮"))
            add(Triple(1003, "相册", "系统相册按钮"))
            add(Triple(1005, "相机", "系统相机按钮"))
            add(Triple(1004, "红包", "发送红包按钮"))
            if (hasBubblesSlot) add(Triple(1016, "泡泡", "泡泡消息按钮"))
            add(Triple(1006, "加号", "更多功能按钮"))
        }
    }

    ConfigPageScaffold(
        title = "快捷栏精简",
        configData = ShortcutConfig(tempIds),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        SelectionGroup {
            options.forEach { (id, label, desc) ->
                SelectionItem(
                    title = label,
                    subtitle = desc,
                    isSelected = tempIds.contains(id),
                    onClick = {
                        val newSet = tempIds.toMutableSet()
                        if (newSet.contains(id)) {
                            newSet.remove(id)
                        } else {
                            newSet.add(id)
                            if (id == 1004) newSet.remove(1016)
                            if (id == 1016) newSet.remove(1004)
                        }
                        tempIds = newSet
                    }
                )
            }
        }
    }
}