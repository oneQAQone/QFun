package me.yxp.qfun.ui.pages.configs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.conf.MenuConfig
import me.yxp.qfun.ui.components.listitems.InputItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.ui.core.theme.QFunTheme

private val MENU_NAME_HINT = listOf(
    "复制", "引用", "转发", "转群相册", "收藏", "删除", "撤回",
    "多选", "部分选中", "全选", "收起文字", "设为精华", "截图", "保存", "存微云",
    "发给电脑", "翻译", "转文字", "听筒播放", "静音播放", "原速", "存表情",
    "相关表情", "问小Q", "由元宝提供", "提醒", "做同款", "接龙", "设为待办",
    "冒泡泡", "发QQ秀", "举报", "调试"
)

@Composable
fun MessageMenuPage(
    currentConfig: MenuConfig,
    onSave: (MenuConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var hiddenNames by remember(currentConfig) { mutableStateOf(currentConfig.hiddenNames) }
    val colors = QFunTheme.colors

    ConfigPageScaffold(
        title = "长按菜单精简",
        configData = MenuConfig(hiddenNames),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "多个名称用英文逗号隔开，匹配到的按钮会被隐藏；本模块添加的功能不会被隐藏",
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            InputItem(
                title = "要隐藏的按钮名称",
                value = hiddenNames,
                onValueChange = { hiddenNames = it },
                placeholder = "例如: 转发,收藏,撤回,复制"
            )

            Text(
                text = "可参考的菜单名称",
                fontSize = 13.sp,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = MENU_NAME_HINT.joinToString("、"),
                fontSize = 12.sp,
                color = colors.textSecondary,
                lineHeight = 20.sp
            )
        }
    }
}