package me.yxp.qfun.ui.pages.configs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.yxp.qfun.conf.SparkConfig
import me.yxp.qfun.ui.components.biz.AsyncSelectorDialog
import me.yxp.qfun.ui.components.dialogs.SelectionItem
import me.yxp.qfun.ui.components.listitems.ActionItem
import me.yxp.qfun.ui.components.listitems.InputItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.utils.qq.FriendTool
import me.yxp.qfun.utils.qq.TroopTool

@Composable
fun AutoKeepSparkPage(
    currentConfig: SparkConfig,
    onSave: (SparkConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var messageText by remember(currentConfig) { mutableStateOf(currentConfig.message) }
    var hourText by remember(currentConfig) {
        mutableStateOf(if (currentConfig.hour == 0) "" else currentConfig.hour.toString())
    }
    var minuteText by remember(currentConfig) {
        mutableStateOf(if (currentConfig.minute == 0) "" else currentConfig.minute.toString())
    }
    var secondText by remember(currentConfig) {
        mutableStateOf(if (currentConfig.second == 0) "" else currentConfig.second.toString())
    }
    var contacts by remember(currentConfig) { mutableStateOf(currentConfig.contacts) }
    var showSelector by remember { mutableStateOf(false) }

    fun buildConfig(): SparkConfig {
        val h = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0
        val m = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
        val s = secondText.toIntOrNull()?.coerceIn(0, 59) ?: 0
        return SparkConfig(
            contacts = contacts,
            message = messageText,
            hour = h,
            minute = m,
            second = s
        )
    }

    if (showSelector) {
        AsyncSelectorDialog(
            title = "选择续火目标",
            currentSelection = contacts,
            dataLoader = {
                val groups = TroopTool.getGroupList().map {
                    SelectionItem("${it.group}(群聊)", it.groupName)
                }
                val friends = FriendTool.getAllFriend().map {
                    SelectionItem("${it.uin}(好友)", it.remark.ifEmpty { it.name })
                }
                groups + friends
            },
            mapper = { it },
            onDismiss = { showSelector = false },
            onConfirm = {
                contacts = it
                showSelector = false
            }
        )
    } else {
        ConfigPageScaffold(
            title = "自动续火配置",
            configData = buildConfig(),
            onSave = onSave,
            onDismiss = onDismiss,
            confirmText = "保存并生效"
        ) { _ ->
            InputItem(
                title = "发送内容",
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = "续火"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputItem(
                    title = "时",
                    value = hourText,
                    onValueChange = { hourText = it },
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                InputItem(
                    title = "分",
                    value = minuteText,
                    onValueChange = { minuteText = it },
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                InputItem(
                    title = "秒",
                    value = secondText,
                    onValueChange = { secondText = it },
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            ActionItem(
                title = "续火目标",
                description = "已选择 ${contacts.size} 个目标",
                onClick = { showSelector = true }
            )
        }
    }
}
