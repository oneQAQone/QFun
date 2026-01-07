package me.yxp.qfun.ui.pages.plugin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.components.atoms.ActionButton
import me.yxp.qfun.ui.components.atoms.ActionButtonStyle
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.atoms.QFunSwitch
import me.yxp.qfun.ui.components.molecules.AnimatedListItem
import me.yxp.qfun.ui.components.molecules.EmptyStateView
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun LocalPluginPage(
    plugins: List<LocalPluginData>,
    onRunToggle: (String, Boolean) -> Unit,
    onAutoLoadToggle: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit,
    onReload: (String) -> Unit,
    onUpload: (String) -> Unit
) {
    if (plugins.isEmpty()) {
        EmptyStateView(message = "暂无本地脚本")
    } else {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plugins, key = { "local_${it.id}" }) { plugin ->
                AnimatedListItem(plugins.indexOf(plugin)) {
                    LocalPluginCard(
                        plugin = plugin,
                        onRunToggle = { onRunToggle(plugin.id, it) },
                        onAutoLoadToggle = { onAutoLoadToggle(plugin.id, it) },
                        onDelete = { onDelete(plugin.id) },
                        onReload = { onReload(plugin.id) },
                        onUpload = { onUpload(plugin.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalPluginCard(
    plugin: LocalPluginData,
    onRunToggle: (Boolean) -> Unit,
    onAutoLoadToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onReload: () -> Unit,
    onUpload: () -> Unit
) {
    val colors = QFunTheme.colors
    var isExpanded by remember { mutableStateOf(false) }

    QFunCard(modifier = Modifier.fillMaxWidth(), animateContentSize = true) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            PluginCardHeader(
                plugin.name,
                plugin.version,
                plugin.isRunning,
                onRunToggle
            ) { isExpanded = !isExpanded }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.textSecondary.copy(0.1f))
                Spacer(modifier = Modifier.height(12.dp))
                PluginCardDetails(
                    plugin.author,
                    plugin.description,
                    plugin.isAutoLoad,
                    onAutoLoadToggle
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                PluginCardActions(onDelete, onReload, onUpload)
            }
        }
    }
}

@Composable
private fun PluginCardHeader(
    name: String,
    version: String,
    isRunning: Boolean,
    onRunToggle: (Boolean) -> Unit,
    onExpandToggle: () -> Unit
) {
    val colors = QFunTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExpandToggle
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "V$version • ${if (isRunning) "运行中" else "未运行"}",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
        QFunSwitch(isRunning, onRunToggle)
    }
}

@Composable
private fun PluginCardDetails(
    author: String,
    description: String,
    isAutoLoad: Boolean,
    onAutoLoadToggle: (Boolean) -> Unit
) {
    val colors = QFunTheme.colors

    Text("作者: $author", fontSize = 13.sp, color = colors.textSecondary)
    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
    Text(
        description.ifEmpty { "暂无描述" },
        fontSize = 13.sp,
        color = colors.textSecondary,
        lineHeight = 18.sp
    )
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "QQ启动时自动加载",
            fontSize = 14.sp,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        QFunSwitch(isAutoLoad, onAutoLoadToggle)
    }
}

@Composable
private fun PluginCardActions(onDelete: () -> Unit, onReload: () -> Unit, onUpload: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        ActionButton("删除", onDelete, style = ActionButtonStyle.Danger)
        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        ActionButton("重载", onReload, style = ActionButtonStyle.Primary)
        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        ActionButton("上传", onUpload, style = ActionButtonStyle.Success)
    }
}
