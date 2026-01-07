package me.yxp.qfun.ui.pages.configs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.yxp.qfun.conf.DeviceConfig
import me.yxp.qfun.ui.components.listitems.InputItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

@Composable
fun CustomDevicePage(
    currentConfig: DeviceConfig,
    onSave: (DeviceConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var fakeModelText by remember(currentConfig) { mutableStateOf(currentConfig.fakeModel) }

    fun buildConfig(): DeviceConfig {
        return DeviceConfig(fakeModel = fakeModelText)
    }

    ConfigPageScaffold(
        title = "设置伪装机型",
        configData = buildConfig(),
        onSave = onSave,
        onDismiss = onDismiss
    ) { _ ->
        InputItem(
            title = "机型名称",
            value = fakeModelText,
            onValueChange = { fakeModelText = it },
            placeholder = "请输入机型",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
