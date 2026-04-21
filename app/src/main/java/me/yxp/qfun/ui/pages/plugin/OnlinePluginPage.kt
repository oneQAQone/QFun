package me.yxp.qfun.ui.pages.plugin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.yxp.qfun.ui.components.atoms.ActionButton
import me.yxp.qfun.ui.components.atoms.ActionButtonStyle
import me.yxp.qfun.ui.components.atoms.LoadingIndicator
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.molecules.AnimatedListItem
import me.yxp.qfun.ui.components.molecules.EmptyStateView
import me.yxp.qfun.ui.components.molecules.PullRefreshBox
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.viewmodel.PluginListUiState
import me.yxp.qfun.utils.ui.HighlightUtils

@Composable
fun OnlinePluginPage(
    uiState: PluginListUiState,
    isOnlineRefreshing: Boolean,
    searchQuery: String,
    listState: LazyListState,
    downloadingPlugins: Set<String>,
    onDownload: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1
        }
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PullRefreshBox(
            isRefreshing = isOnlineRefreshing,
            onRefresh = onRefresh
        ) {
            when (uiState) {
                is PluginListUiState.Loading -> {
                    LoadingIndicator(
                        message = "正在获取在线脚本..."
                    )
                }

                is PluginListUiState.Error -> {
                    EmptyStateView(
                        message = "获取失败: ${uiState.message}\n点击重试",
                        onClick = onRefresh
                    )
                }

                is PluginListUiState.Success -> {
                    OnlinePluginList(
                        plugins = uiState.data,
                        searchQuery = searchQuery,
                        downloadingPlugins = downloadingPlugins,
                        onDownload = onDownload,
                        onRefresh = onRefresh,
                        listState = listState
                    )
                }
            }
        }

        ScrollToTopButton(
            visible = showScrollToTop && uiState is PluginListUiState.Success,
            onClick = { 
                scope.launch { 
                    listState.animateScrollToItem(index = 0) 
                } 
            },
            modifier = Modifier
                .align(
                    alignment = Alignment.BottomEnd
                )
                .padding(
                    end = 16.dp, 
                    bottom = 100.dp
                )
        )
    }
}

@Composable
private fun OnlinePluginList(
    plugins: List<OnlinePluginData>,
    searchQuery: String,
    downloadingPlugins: Set<String>,
    onDownload: (String) -> Unit,
    onRefresh: () -> Unit,
    listState: LazyListState
) {
    val colors = QFunTheme.colors

    if (plugins.isEmpty()) {
        EmptyStateView(
            message = if (searchQuery.isEmpty()) {
                "暂无在线脚本\n点击刷新"
            } else {
                "未找到匹配的在线脚本"
            },
            onClick = onRefresh
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 150.dp
            ),
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp
            ),
            userScrollEnabled = true 
        ) {
            items(
                items = plugins,
                key = { "online_${it.id}" }
            ) { plugin ->
                val hName = HighlightUtils.highlightText(
                    text = plugin.name,
                    query = searchQuery,
                    highlightColor = colors.accentBlue,
                    baseColor = colors.textPrimary
                )

                val hAuthor = HighlightUtils.highlightText(
                    text = "作者: ${plugin.author}",
                    query = searchQuery,
                    highlightColor = colors.accentBlue,
                    baseColor = colors.textSecondary
                )

                val hDesc = HighlightUtils.highlightText(
                    text = plugin.description.ifEmpty { "暂无描述" },
                    query = searchQuery,
                    highlightColor = colors.accentBlue,
                    baseColor = colors.textSecondary
                )

                AnimatedListItem(
                    index = plugins.indexOf(element = plugin)
                ) {
                    OnlinePluginCard(
                        plugin = plugin,
                        highlightedName = hName,
                        highlightedAuthor = hAuthor,
                        highlightedDesc = hDesc,
                        isDownloading = downloadingPlugins.contains(element = plugin.id),
                        onDownload = {
                            onDownload(plugin.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlinePluginCard(
    plugin: OnlinePluginData,
    highlightedName: AnnotatedString,
    highlightedAuthor: AnnotatedString,
    highlightedDesc: AnnotatedString,
    isDownloading: Boolean,
    onDownload: () -> Unit
) {
    val colors = QFunTheme.colors
    
    var isExpanded by remember {
        mutableStateOf(value = false)
    }

    QFunCard(
        modifier = Modifier.fillMaxWidth(),
        animateContentSize = true
    ) {
        Column(
            modifier = Modifier.padding(
                all = Dimens.PaddingMedium
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { 
                            MutableInteractionSource() 
                        },
                        indication = null,
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(
                        weight = 1f
                    )
                ) {
                    Text(
                        text = highlightedName, 
                        fontSize = 17.sp, 
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(
                            height = 4.dp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "下载: ${plugin.downloads}", 
                            fontSize = 12.sp, 
                            color = colors.textSecondary
                        )

                        Spacer(
                            modifier = Modifier.width(
                                width = 8.dp
                            )
                        )

                        Text(
                            text = "V${plugin.version}", 
                            fontSize = 12.sp, 
                            color = colors.accentGreen, 
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                ActionButton(
                    text = if (isDownloading) {
                        "下载中..."
                    } else {
                        "下载"
                    },
                    onClick = {
                        if (!isDownloading) {
                            onDownload()
                        }
                    },
                    style = ActionButtonStyle.Success
                )
            }

            if (isExpanded) {
                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )

                HorizontalDivider(
                    color = colors.textSecondary.copy(
                        alpha = 0.1f
                    )
                )

                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = highlightedAuthor, 
                        fontSize = 13.sp, 
                        modifier = Modifier.weight(
                            weight = 1f
                        )
                    )

                    Text(
                        text = plugin.uploadTime, 
                        fontSize = 12.sp, 
                        color = colors.textSecondary
                    )
                }

                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )

                Text(
                    text = "脚本介绍：", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = colors.textPrimary
                )

                Spacer(
                    modifier = Modifier.height(
                        height = 4.dp
                    )
                )

                Text(
                    text = highlightedDesc, 
                    fontSize = 13.sp, 
                    lineHeight = 18.sp
                )
            }
        }
    }
}