package me.yxp.qfun.ui.pages.configs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.SignatureConfig
import me.yxp.qfun.ui.components.atoms.DialogTextField
import me.yxp.qfun.ui.components.atoms.QFunSwitch
import me.yxp.qfun.ui.components.atoms.QFunTextField
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainer
import me.yxp.qfun.ui.components.dialogs.ConfirmDialog
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import java.io.File

@Composable
private fun RoundedSelectionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AccentBlue else colors.textPrimary,
        animationSpec = tween(durationMillis = 200),
        label = "selectionTextColor"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = colors.textSecondary)
        }
        if (isSelected) {
            Text(text = "✓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
        }
    }
}

@Composable
fun CustomInputItemWithInfo(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    onInfoClick: () -> Unit
) {
    val colors = QFunTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(1.dp, colors.textSecondary, CircleShape)
                    .clickable(onClick = onInfoClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "i", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Black, 
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-3.5).dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        QFunTextField(
            value = value,
            onValueChange = onValueChange,
            hint = placeholder
        )
    }
}

@Composable
private fun CompactTimeInput(value: String, onValueChange: (String) -> Unit, hint: String) {
    val colors = QFunTheme.colors
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= 2) onValueChange(it) },
        textStyle = TextStyle(
            fontSize = 14.sp, 
            color = colors.textPrimary, 
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(46.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.textSecondary.copy(0.2f), RoundedCornerShape(8.dp)),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(hint, color = colors.textSecondary.copy(0.5f), fontSize = 13.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun AutoSignaturePage(
    currentConfig: SignatureConfig,
    onSave: (SignatureConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = QFunTheme.colors
    
    var mode by remember(currentConfig) { mutableStateOf(currentConfig.mode) }
    var jsonPathText by remember(currentConfig) { mutableStateOf(currentConfig.jsonPath) }
    var apiUrlText by remember(currentConfig) { mutableStateOf(currentConfig.apiUrl) }
    var jsonKeyText by remember(currentConfig) { mutableStateOf(currentConfig.jsonKey) }
    var hourText by remember(currentConfig) { mutableStateOf(if (currentConfig.hour == 0) "" else currentConfig.hour.toString()) }
    var minuteText by remember(currentConfig) { mutableStateOf(if (currentConfig.minute == 0) "" else currentConfig.minute.toString()) }
    var secondText by remember(currentConfig) { mutableStateOf(if (currentConfig.second == 0) "" else currentConfig.second.toString()) }
    
    var notifyOnUpdate by remember(currentConfig) { mutableStateOf(currentConfig.notifyOnUpdate) }
    var notifyText by remember(currentConfig) { mutableStateOf(currentConfig.notifyText) }

    var showNotifyTextDialog by remember { mutableStateOf(false) }
    var showApiInfo by remember { mutableStateOf(false) }
    var showKeyInfo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ModuleScope.launchIO("CopyTxt") {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val destFile = File("${QQCurrentEnv.currentDir}data/signature.txt")
                    FileUtils.ensureFile(destFile)
                    inputStream?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    jsonPathText = destFile.absolutePath
                    Toasts.qqToast(2, "文件已导入")
                } catch (e: Exception) {
                    Toasts.qqToast(1, "导入失败: ${e.message}")
                }
            }
        }
    }

    fun buildConfig(): SignatureConfig {
        val h = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0
        val m = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
        val s = secondText.toIntOrNull()?.coerceIn(0, 59) ?: 0
        return SignatureConfig(
            mode = mode,
            jsonPath = jsonPathText,
            apiUrl = apiUrlText,
            jsonKey = jsonKeyText,
            hour = h,
            minute = m,
            second = s,
            notifyOnUpdate = notifyOnUpdate,
            notifyText = notifyText
        )
    }

    ConfigPageScaffold(
        title = "配置",
        configData = buildConfig(),
        onSave = onSave,
        onDismiss = onDismiss,
        confirmText = "保存"
    ) { _ ->
        
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            RoundedSelectionItem(
                title = "网络导入",
                subtitle = "接入 API 获取文本或解析JSON",
                isSelected = mode == 1,
                onClick = { mode = 1 }
            )
            RoundedSelectionItem(
                title = "本地导入",
                subtitle = if (jsonPathText.isEmpty()) "读取本地TXT文本 (点击选择)" else "已选择: ${File(jsonPathText).name} (点击重新选择)",
                isSelected = mode == 0,
                onClick = {
                    mode = 0
                    launcher.launch("text/plain")
                }
            )
        }

        AnimatedVisibility(
            visible = mode == 1,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(200)),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomInputItemWithInfo(
                    title = "API 链接",
                    value = apiUrlText,
                    onValueChange = { apiUrlText = it },
                    placeholder = "http(s)://...",
                    onInfoClick = { showApiInfo = true }
                )
                CustomInputItemWithInfo(
                    title = "解析 Key ",
                    value = jsonKeyText,
                    onValueChange = { jsonKeyText = it },
                    placeholder = "留空则使用返回的完整文本",
                    onInfoClick = { showKeyInfo = true }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "执行时间", 
                fontSize = 15.sp, 
                fontWeight = FontWeight.Medium, 
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            CompactTimeInput(value = hourText, onValueChange = { hourText = it }, hint = "时")
            Text(" : ", color = colors.textSecondary, modifier = Modifier.padding(horizontal = 4.dp))
            CompactTimeInput(value = minuteText, onValueChange = { minuteText = it }, hint = "分")
            Text(" : ", color = colors.textSecondary, modifier = Modifier.padding(horizontal = 4.dp))
            CompactTimeInput(value = secondText, onValueChange = { secondText = it }, hint = "秒")
        }

        HorizontalDivider(color = colors.textSecondary.copy(alpha = 0.1f))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (notifyOnUpdate) {
                            showNotifyTextDialog = true
                        } else {
                            notifyOnUpdate = true
                        }
                    }
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "更改时是否通知", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (notifyOnUpdate) "提示内容: $notifyText (点击修改)" else "签名更新成功后发送Toast提示",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
                QFunSwitch(checked = notifyOnUpdate, onCheckedChange = { notifyOnUpdate = it })
            }
        }
    }

    if (showNotifyTextDialog) {
        CenterDialog(visible = true, onDismiss = { showNotifyTextDialog = false }) {
            var tempText by remember { mutableStateOf(notifyText) }
            CenterDialogContainer(
                title = "修改提示内容",
                onDismiss = { showNotifyTextDialog = false },
                onConfirm = {
                    notifyText = tempText
                    showNotifyTextDialog = false
                }
            ) {
                DialogTextField(
                    value = tempText,
                    onValueChange = { tempText = it },
                    label = "提示内容",
                    hint = "请输入提示内容"
                )
            }
        }
    }

    ConfirmDialog(
        visible = showApiInfo,
        title = "API 链接提示",
        message = "如果该平台强制要求必须带密钥才能请求，且鉴权方式是 URL 参数，你可能需要将其拼接在后面，例如：\n\nhttps://uapis.cn/api/v1/saying?token=uapi-xxx\n\n具体的参数名 token 或 key 视该平台的具体文档而定。",
        confirmText = "我知道了",
        dismissText = "",
        onDismiss = { showApiInfo = false },
        onConfirm = { showApiInfo = false }
    )

    ConfirmDialog(
        visible = showKeyInfo,
        title = "解析 Key 提示",
        message = "读取 JSON 里的哪个字段。例如接口返回：\n\n{\"text\": \"你好\"}\n\n则此处应填写 text。",
        confirmText = "我知道了",
        dismissText = "",
        onDismiss = { showKeyInfo = false },
        onConfirm = { showKeyInfo = false }
    )
}