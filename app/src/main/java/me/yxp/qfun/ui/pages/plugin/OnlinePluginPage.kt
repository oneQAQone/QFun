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
import me.yxp.qfun.ui.components.atoms.LoadingIndicator
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.molecules.AnimatedListItem
import me.yxp.qfun.ui.components.molecules.EmptyStateView
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.viewmodel.PluginListUiState

@Composable
fun OnlinePluginPage(
    uiState: PluginListUiState,
    downloadingPlugins: Set<String>,
    onDownload: (String) -> Unit,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is PluginListUiState.Loading -> LoadingIndicator(message = "正在获取在线脚本...")
        is PluginListUiState.Error -> EmptyStateView(
            "获取失败: ${uiState.message}\n点击重试",
            onClick = onRefresh
        )

        is PluginListUiState.Success -> OnlinePluginList(
            uiState.data,
            downloadingPlugins,
            onDownload,
            onRefresh
        )
    }
}

@Composable
private fun OnlinePluginList(
    plugins: List<OnlinePluginData>,
    downloadingPlugins: Set<String>,
    onDownload: (String) -> Unit,
    onRefresh: () -> Unit
) {
    if (plugins.isEmpty()) {
        EmptyStateView("暂无在线脚本\n点击刷新", onClick = onRefresh)
    } else {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plugins, key = { "online_${plugins.indexOf(it)}_${it.id}" }) { plugin ->
                AnimatedListItem(plugins.indexOf(plugin)) {
                    OnlinePluginCard(plugin, downloadingPlugins.contains(plugin.id)) {
                        onDownload(
                            plugin.id
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnlinePluginCard(
    plugin: OnlinePluginData,
    isDownloading: Boolean,
    onDownload: () -> Unit
) {
    val colors = QFunTheme.colors
    var isExpanded by remember { mutableStateOf(false) }

    QFunCard(modifier = Modifier.fillMaxWidth(), animateContentSize = true) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            OnlinePluginCardHeader(
                plugin.name,
                plugin.version,
                plugin.downloads,
                isDownloading,
                onDownload
            ) { isExpanded = !isExpanded }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.textSecondary.copy(0.1f))
                Spacer(modifier = Modifier.height(12.dp))
                OnlinePluginCardDetails(plugin.author, plugin.uploadTime, plugin.description)
            }
        }
    }
}

@Composable
private fun OnlinePluginCardHeader(
    name: String,
    version: String,
    downloads: Int,
    isDownloading: Boolean,
    onDownload: () -> Unit,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("下载量: $downloads", fontSize = 12.sp, color = colors.textSecondary)
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Text(
                    "V$version",
                    fontSize = 12.sp,
                    color = colors.accentGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        ActionButton(
            if (isDownloading) "下载中..." else "下载",
            { if (!isDownloading) onDownload() },
            style = ActionButtonStyle.Success
        )
    }
}

@Composable
private fun OnlinePluginCardDetails(author: String, uploadTime: String, description: String) {
    val colors = QFunTheme.colors

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "作者: $author",
            fontSize = 13.sp,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(uploadTime, fontSize = 12.sp, color = colors.textSecondary)
    }

    Spacer(modifier = Modifier.height(12.dp))
    Text("脚本介绍：", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        description.ifEmpty { "暂无描述" },
        fontSize = 13.sp,
        color = colors.textSecondary,
        lineHeight = 18.sp
    )
}
