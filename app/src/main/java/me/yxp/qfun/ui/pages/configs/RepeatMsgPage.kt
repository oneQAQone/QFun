package me.yxp.qfun.ui.pages.configs

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.yxp.qfun.R
import me.yxp.qfun.activity.SettingActivity
import me.yxp.qfun.conf.RepeatConfig
import me.yxp.qfun.ui.components.listitems.ActionItem
import me.yxp.qfun.ui.components.listitems.SwitchItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.qq.QQCurrentEnv

@Composable
fun RepeatMsgPage(
    currentConfig: RepeatConfig,
    bitmap: Bitmap?,
    onSave: (RepeatConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = QQCurrentEnv.activity
    var tempConfig by remember(currentConfig) { mutableStateOf(currentConfig) }
    val colors = QFunTheme.colors

    ConfigPageScaffold(
        title = "复读配置",
        configData = tempConfig,
        onSave = onSave,
        onDismiss = onDismiss
    ) { _ ->
        SwitchItem(
            title = "快捷图标",
            description = "在消息气泡旁显示复读按钮",
            checked = tempConfig.enableIcon,
            onCheckedChange = { tempConfig = tempConfig.copy(enableIcon = it) }
        )

        AnimatedVisibility(
            visible = tempConfig.enableIcon,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SwitchItem(
                    title = "双击触发",
                    description = "防止误触，需双击图标复读",
                    checked = tempConfig.doubleClick,
                    onCheckedChange = { tempConfig = tempConfig.copy(doubleClick = it) }
                )

                ActionItem(
                    title = "自定义图标",
                    description = if (bitmap != null) "已设置自定义图标" else "当前使用默认图标",
                    onClick = {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        context?.startActivityForResult(
                            intent,
                            SettingActivity.REQUEST_CODE_IMAGE
                        )
                        onDismiss()
                    },
                    trailingContent = {
                        if (bitmap != null) {
                            Icon(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_action_repeat),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = colors.textSecondary
                            )
                        }
                    }
                )
            }
        }

        SwitchItem(
            title = "长按菜单",
            description = "长按消息在菜单中显示复读选项",
            checked = tempConfig.enableMenu,
            onCheckedChange = { tempConfig = tempConfig.copy(enableMenu = it) }
        )

        SwitchItem(
            title = "右滑复读",
            description = "向右拖动消息气泡，进度环填满后松手即可复读",
            checked = tempConfig.enableSwipe,
            onCheckedChange = { tempConfig = tempConfig.copy(enableSwipe = it) }
        )
    }
}
