package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.DrawerConfig
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun SettingMeDrawerPage(
    currentConfig: DrawerConfig,
    onSave: (DrawerConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var tempIds by remember(currentConfig) { mutableStateOf(currentConfig.hiddenIds) }

    val options = remember {
        listOf(
            "d_album" to "相册",
            "d_favorite" to "收藏",
            "d_document" to "文件",
            "d_qqwallet" to "钱包",
            "d_vip_identity" to "会员中心",
            "d_decoration" to "个性装扮",
            "d_vip_card" to "免流量"
        )
    }

    ConfigPageScaffold(
        title = "侧边栏精简",
        configData = DrawerConfig(tempIds),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        SelectionGroup {
            options.forEach { (id, label) ->
                SelectionItem(
                    title = label,
                    subtitle = id,
                    isSelected = tempIds.contains(id),
                    onClick = {
                        val newSet = tempIds.toMutableSet()
                        if (!newSet.remove(id)) newSet.add(id)
                        tempIds = newSet
                    }
                )
            }
        }
    }
}
