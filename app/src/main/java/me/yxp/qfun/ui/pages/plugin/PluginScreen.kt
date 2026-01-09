package me.yxp.qfun.ui.pages.plugin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.molecules.QFunTopBar
import me.yxp.qfun.ui.components.molecules.TabItem
import me.yxp.qfun.ui.components.molecules.TopBarIconButton
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.viewmodel.PluginListUiState

@Immutable
data class LocalPluginData(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isRunning: Boolean,
    val isAutoLoad: Boolean
)

@Immutable
data class OnlinePluginData(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val downloads: Int,
    val uploadTime: String
)

@Composable
fun PluginScreen(
    localPlugins: List<LocalPluginData>,
    onlineUiState: PluginListUiState,
    downloadingPlugins: Set<String>,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBackClick: () -> Unit,
    onCreatePlugin: () -> Unit,
    onDocsClick: () -> Unit,
    onRefreshOnline: () -> Unit,
    onPluginRunToggle: (String, Boolean) -> Unit,
    onPluginAutoLoadToggle: (String, Boolean) -> Unit,
    onPluginDelete: (String) -> Unit,
    onPluginReload: (String) -> Unit,
    onPluginUpload: (String) -> Unit,
    onPluginDownload: (String) -> Unit,
    onPickIcon: () -> Unit
) {
    val colors = QFunTheme.colors
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        QFunTopBar(
            "Java Plugin",
            showBackButton = true,
            onBackClick = onBackClick,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            actions = {
                TopBarIconButton(
                    iconRes = R.drawable.ic_settings_gear,
                    contentDescription = "设置悬浮图标",
                    onClick = onPickIcon
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
        PluginTabBar(selectedTab, { selectedTab = it }, onCreatePlugin, onDocsClick)
        Spacer(modifier = Modifier.height(12.dp))
        when (selectedTab) {
            0 -> LocalPluginPage(
                localPlugins,
                onPluginRunToggle,
                onPluginAutoLoadToggle,
                onPluginDelete,
                onPluginReload,
                onPluginUpload
            )

            1 -> OnlinePluginPage(
                onlineUiState,
                downloadingPlugins,
                onPluginDownload,
                onRefreshOnline
            )
        }
    }
}

@Composable
private fun PluginTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onCreatePlugin: () -> Unit,
    onDocsClick: () -> Unit
) {
    QFunCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabItem("本地脚本", selectedTab == 0, { onTabSelected(0) })
            TabItem("在线脚本", selectedTab == 1, { onTabSelected(1) })
            TabItem("创建脚本", false, onCreatePlugin)
            TabItem("开发文档", false, onDocsClick)
        }
    }
}