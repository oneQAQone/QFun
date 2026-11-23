package me.yxp.qfun.ui.pages.configs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import me.yxp.qfun.conf.IceBreakWelcomeConfig
import me.yxp.qfun.conf.OthersJoinMode
import me.yxp.qfun.ui.components.listitems.PreferenceSection
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.listitems.SwitchItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun IceBreakWelcomePage(
    currentConfig: IceBreakWelcomeConfig,
    onSave: (IceBreakWelcomeConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var tempConfig by remember(currentConfig) { mutableStateOf(currentConfig) }

    ConfigPageScaffold(
        title = "入群欢迎净化",
        configData = tempConfig,
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        PreferenceSection(title = "自己入群") {
            SwitchItem(
                title = "隐藏新人欢迎卡片",
                description = "进群后不再插入那张大卡片（含群文件、群相册、打个招呼、欢迎语、换一换）",
                checked = tempConfig.hideSelfWelcomeCard,
                onCheckedChange = { tempConfig = tempConfig.copy(hideSelfWelcomeCard = it) }
            )
        }

        PreferenceSection(title = "他人入群（灰条上的「欢迎」）") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SelectionItem(
                    title = "完整保留",
                    subtitle = "「欢迎」入口和自动弹开面板都保留",
                    isSelected = tempConfig.othersJoinMode == OthersJoinMode.KEEP_ALL,
                    onClick = { tempConfig = tempConfig.copy(othersJoinMode = OthersJoinMode.KEEP_ALL) }
                )
                SelectionItem(
                    title = "只禁止自动展开",
                    subtitle = "灰条上仍有「欢迎」，点它才会展开面板",
                    isSelected = tempConfig.othersJoinMode == OthersJoinMode.NO_AUTO_EXPAND,
                    onClick = {
                        tempConfig = tempConfig.copy(othersJoinMode = OthersJoinMode.NO_AUTO_EXPAND)
                    }
                )
                SelectionItem(
                    title = "彻底隐藏入口",
                    subtitle = "灰条上不再出现「欢迎」，也不会有面板",
                    isSelected = tempConfig.othersJoinMode == OthersJoinMode.HIDE_ENTRY,
                    onClick = {
                        tempConfig = tempConfig.copy(othersJoinMode = OthersJoinMode.HIDE_ENTRY)
                    }
                )
            }
        }
    }
}
