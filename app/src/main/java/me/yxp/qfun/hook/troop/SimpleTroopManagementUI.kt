package me.yxp.qfun.hook.troop

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.components.atoms.DialogTextField
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainer
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.AccentGreen
import me.yxp.qfun.ui.core.theme.AccentRed
import me.yxp.qfun.ui.core.theme.QFunTheme

internal data class ManagementButtonData(
    val text: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
internal fun TroopManagementContent(
    activity: Activity,
    memberUin: String,
    memberNick: String,
    isOwner: Boolean,
    onEnterProfile: () -> Unit,
    onRecall: () -> Unit,
    onSetAdmin: () -> Unit,
    onCancelAdmin: () -> Unit,
    onSetMute: (Long) -> Unit,
    onCancelMute: () -> Unit,
    onSetTitle: (String) -> Unit,
    onSetCard: (String) -> Unit,
    onKick: () -> Unit,
    onKickBlock: () -> Unit,
    onMuteAll: () -> Unit,
    onUnmuteAll: () -> Unit,
    getCurrentCard: () -> String
) {
    val colors = QFunTheme.colors
    val context = LocalContext.current

    val managementButtons = buildList {
        add(ManagementButtonData("进入主页", AccentBlue, onEnterProfile))
        add(ManagementButtonData("撤回消息", AccentBlue, onRecall))
        add(ManagementButtonData("设置管理", AccentGreen, onSetAdmin))
        add(ManagementButtonData("取消管理", AccentGreen, onCancelAdmin))
        add(ManagementButtonData("设置禁言", AccentBlue) {
            showInputDialog(
                activity,
                "设置禁言时长",
                "禁言时长（秒）",
                "请输入秒数",
                "0",
                KeyboardType.Number
            ) {
                onSetMute(it.toLongOrNull() ?: 0)
            }
        })
        add(ManagementButtonData("解除禁言", AccentBlue, onCancelMute))
        add(ManagementButtonData("设置头衔", AccentBlue) {
            if (isOwner) showInputDialog(
                activity,
                "设置头衔",
                "专属头衔",
                "请输入头衔"
            ) { onSetTitle(it) }
        })
        add(ManagementButtonData("设置群名", AccentBlue) {
            showInputDialog(
                activity,
                "设置群昵称",
                "群昵称",
                "请输入群昵称",
                getCurrentCard()
            ) { onSetCard(it) }
        })
        add(ManagementButtonData("踢出此人", AccentRed, onKick))
        add(ManagementButtonData("黑踢此人", AccentRed, onKickBlock))
        add(ManagementButtonData("全体禁言", AccentRed, onMuteAll))
        add(ManagementButtonData("全体解禁", AccentGreen, onUnmuteAll))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            "群成员管理",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(20.dp))
        InfoRow("QQ号", memberUin) {
            copyToClipboard(context, memberUin)
            Toast.makeText(context, "已复制QQ号", Toast.LENGTH_SHORT).show()
        }
        Spacer(modifier = Modifier.height(10.dp))
        InfoRow("群昵称", memberNick) {
            copyToClipboard(context, memberNick)
            Toast.makeText(context, "已复制群昵称", Toast.LENGTH_SHORT).show()
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(managementButtons) { button ->
                ManagementButton(button.text, button.color, button.onClick)
            }
        }
    }
}

internal fun showInputDialog(
    activity: Activity,
    title: String,
    label: String,
    hint: String,
    initialValue: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onConfirm: (String) -> Unit
) {
    QFunCenterDialog(activity) { dismiss ->
        var text by remember { mutableStateOf(initialValue) }

        CenterDialogContainer(title, dismiss, { onConfirm(text); dismiss() }) {
            DialogTextField(
                text,
                {
                    text =
                        if (keyboardType == KeyboardType.Number) it.filter { c -> c.isDigit() } else it
                },
                label = label,
                hint = hint,
                keyboardType = keyboardType
            )
        }
    }.show()
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("QFun", text))
}

@Composable
private fun InfoRow(label: String, value: String, onClick: () -> Unit) {
    val colors = QFunTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$label: ", fontSize = 15.sp, color = colors.textSecondary)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("📋", fontSize = 12.sp, color = colors.textSecondary)
    }
}

@Composable
private fun ManagementButton(text: String, color: Color, onClick: () -> Unit) {
    val colors = QFunTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = color)
    }
}
