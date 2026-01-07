package me.yxp.qfun.ui.pages.configs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import me.yxp.qfun.conf.TimeConfig
import me.yxp.qfun.ui.components.listitems.InputItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.ui.core.theme.AccentRed
import me.yxp.qfun.ui.core.theme.QFunTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun ShowMsgTimePage(
    currentConfig: TimeConfig,
    onSave: (TimeConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var formatText by remember(currentConfig) { mutableStateOf(currentConfig.format) }
    var colorText by remember(currentConfig) {
        mutableStateOf(
            String.format(
                "#%08X",
                currentConfig.color
            )
        )
    }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val colors = QFunTheme.colors

    fun parseColor(input: String): Int? {
        return try {
            val clean = input.replace("[^a-fA-F0-9]".toRegex(), "")
            when (clean.length) {
                6 -> "#FF$clean".toColorInt()
                8 -> "#$clean".toColorInt()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun validateFormat(format: String): Boolean {
        return try {
            SimpleDateFormat(format, Locale.getDefault())
            true
        } catch (_: Exception) {
            false
        }
    }

    fun buildConfig(): TimeConfig {
        val parsedColor = parseColor(colorText) ?: currentConfig.color
        val finalFormat = if (validateFormat(formatText)) formatText else currentConfig.format
        return TimeConfig(format = finalFormat, color = parsedColor)
    }

    val previewColor = parseColor(colorText) ?: currentConfig.color

    ConfigPageScaffold(
        title = "时间格式设置",
        configData = buildConfig(),
        onSave = onSave,
        onDismiss = onDismiss
    ) { _ ->
        InputItem(
            title = "颜色（十六进制）",
            value = colorText,
            onValueChange = { newValue ->
                colorText = newValue
                errorMsg = if (parseColor(newValue) == null && newValue.isNotEmpty()) {
                    "颜色格式无效"
                } else {
                    null
                }
            },
            placeholder = "#FF0000FF"
        )
        InputItem(
            title = "时间格式",
            value = formatText,
            onValueChange = { newValue ->
                formatText = newValue
                errorMsg = if (!validateFormat(newValue) && newValue.isNotEmpty()) {
                    "时间格式无效"
                } else {
                    null
                }
            },
            placeholder = "HH:mm:ss"
        )

        Text("预览", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
        Text(
            text = if (validateFormat(formatText)) {
                SimpleDateFormat(formatText, Locale.getDefault()).format(Date())
            } else {
                "格式错误"
            },
            fontSize = 16.sp,
            color = ComposeColor(previewColor)
        )

        if (errorMsg != null) {
            Text(errorMsg!!, fontSize = 13.sp, color = AccentRed)
        }
    }
}
