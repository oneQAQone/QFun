package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.SummaryConfig
import me.yxp.qfun.ui.components.listitems.InputItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun ModifyPicSummaryPage(
    currentConfig: SummaryConfig,
    onSave: (SummaryConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var keyText by remember(currentConfig) { mutableStateOf(currentConfig.key) }
    var summaryOrUrlText by remember(currentConfig) { mutableStateOf(currentConfig.summaryOrUrl) }

    fun buildConfig(): SummaryConfig {
        return SummaryConfig(key = keyText, summaryOrUrl = summaryOrUrlText)
    }

    ConfigPageScaffold(
        title = "设置图片外显文本",
        configData = buildConfig(),
        onSave = onSave,
        onDismiss = onDismiss,
        confirmText = "保存"
    ) { _ ->
        InputItem(
            title = "需要显示的key（支持深度查找）",
            value = keyText,
            onValueChange = { keyText = it },
            placeholder = "留空则显示整个响应"
        )
        InputItem(
            title = "普通外显或API链接",
            value = summaryOrUrlText,
            onValueChange = { summaryOrUrlText = it },
            placeholder = "输入文本或http(s)://..."
        )
    }
}
