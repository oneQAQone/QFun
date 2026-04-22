package me.yxp.qfun.ui.pages.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.SocialButton
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.listitems.SwitchActionCard
import me.yxp.qfun.ui.components.molecules.AnimatedListItem
import me.yxp.qfun.ui.components.molecules.SearchTopBar
import me.yxp.qfun.ui.components.molecules.TopBarMenuItem
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.configs.ConfigUiRegistry
import me.yxp.qfun.ui.pages.settings.components.CategoryCard
import me.yxp.qfun.ui.viewmodel.UpdateLogState
import me.yxp.qfun.utils.ui.HighlightUtils

@Immutable
data class CategoryData(val name: String, val items: List<FunctionData>)

@Immutable
data class FunctionData(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean,
    val isAvailable: Boolean,
    val isClickable: Boolean,
    val configKey: String? = null
)

@Composable
fun SettingsScreen(
    categories: List<CategoryData>,
    searchResults: List<Pair<FunctionData, String>>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    versionInfo: String,
    themeMode: Int,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onImportConfig: () -> Unit,
    onExportConfig: () -> Unit,
    onFunctionToggle: (String, Boolean) -> Unit,
    onFunctionClick: (String) -> Unit,
    onGithubClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onGroupClick: () -> Unit,
    onDonateClick: () -> Unit,
    activeConfigKey: String?,
    onDismissDialog: () -> Unit,
    showUpdateLogDialog: Boolean,
    updateLogState: UpdateLogState,
    onUpdateLogClick: () -> Unit,
    onDismissUpdateLog: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val mainListState = rememberLazyListState()

    BackHandler(activeConfigKey != null, onDismissDialog)
    BackHandler(activeConfigKey == null && isSearchActive) { onSearchActiveChange(false) }
    BackHandler(activeConfigKey == null && !isSearchActive && selectedCategoryName != null) {
        selectedCategoryName = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchTopBar(
                title = selectedCategoryName ?: "QFun",
                searchQuery = searchQuery,
                onQueryChange = onQueryChange,
                isSearchActive = isSearchActive,
                onSearchActiveChange = onSearchActiveChange,
                showBackButton = true,
                onBackClick = {
                    if (selectedCategoryName != null) selectedCategoryName = null else onBackClick()
                },
                themeMode = themeMode,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                searchHint = "搜索功能名称或描述...",
                menuItems = listOf(
                    TopBarMenuItem(
                        "导入配置",
                        R.drawable.ic_file_import,
                        onImportConfig
                    ),
                    TopBarMenuItem(
                        "导出配置",
                        R.drawable.ic_file_export,
                        onExportConfig
                    ),
                    TopBarMenuItem(
                        "更新日志",
                        R.drawable.ic_update_log,
                        onUpdateLogClick
                    )
                )
            )

            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ContentTransition"
            ) { searching ->
                if (searching) {
                    SearchModeContent(
                        searchResults = searchResults,
                        searchQuery = searchQuery,
                        onFunctionToggle = onFunctionToggle,
                        onFunctionClick = onFunctionClick
                    )
                } else {
                    if (selectedCategoryName == null) {
                        MainPage(
                            categories = categories,
                            versionInfo = versionInfo,
                            onCategoryClick = { selectedCategoryName = it.name },
                            onGithubClick = onGithubClick,
                            onTelegramClick = onTelegramClick,
                            onGroupClick = onGroupClick,
                            onDonateClick = onDonateClick,
                            listState = mainListState
                        )
                    } else {
                        categories.find { it.name == selectedCategoryName }?.let { category ->
                            DetailPage(
                                category = category,
                                onFunctionToggle = onFunctionToggle,
                                onFunctionClick = onFunctionClick
                            )
                        }
                    }
                }
            }
        }

        ConfigDialogOverlay(activeConfigKey, onDismissDialog)
    }

    UpdateLogDialogContent(
        visible = showUpdateLogDialog,
        state = updateLogState,
        onDismiss = onDismissUpdateLog
    )
}

@Composable
private fun SearchModeContent(
    searchResults: List<Pair<FunctionData, String>>,
    searchQuery: String,
    onFunctionToggle: (String, Boolean) -> Unit,
    onFunctionClick: (String) -> Unit
) {
    val colors = QFunTheme.colors

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.PaddingMedium, Dimens.PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (searchQuery.isNotEmpty() && searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "未找到相关功能",
                        color = colors.textSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        }

        items(searchResults, key = { it.first.id }) { (item, categoryName) ->
            val highlightedTitle = HighlightUtils.highlightText(
                text = item.name,
                query = searchQuery,
                highlightColor = colors.accentBlue,
                baseColor = colors.textPrimary
            )
            val highlightedDesc = HighlightUtils.highlightText(
                text = "[$categoryName] · ${item.description}",
                query = searchQuery,
                highlightColor = colors.accentBlue,
                baseColor = colors.textSecondary
            )

            SwitchActionCard(
                title = highlightedTitle,
                subtitle = highlightedDesc,
                isChecked = item.isEnabled,
                onCheckedChange = { onFunctionToggle(item.id, it) },
                isAvailable = item.isAvailable,
                onClick = if (item.isClickable) { { onFunctionClick(item.id) } } else null
            )
        }
    }
}

@Composable
private fun MainPage(
    categories: List<CategoryData>,
    versionInfo: String,
    onCategoryClick: (CategoryData) -> Unit,
    onGithubClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onGroupClick: () -> Unit,
    onDonateClick: () -> Unit,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }
        items(categories, key = { it.name }) { category ->
            AnimatedListItem(categories.indexOf(category)) {
                CategoryCard(
                    name = category.name,
                    totalCount = category.items.size,
                    enabledCount = category.items.count { it.isEnabled },
                    onClick = { onCategoryClick(category) },
                    modifier = Modifier.padding(Dimens.PaddingMedium, 6.dp)
                )
            }
        }
        item {
            AboutSection(
                versionInfo = versionInfo,
                onGithubClick = onGithubClick,
                onTelegramClick = onTelegramClick,
                onGroupClick = onGroupClick,
                onDonateClick = onDonateClick
            )
        }
    }
}

@Composable
private fun DetailPage(
    category: CategoryData,
    onFunctionToggle: (String, Boolean) -> Unit,
    onFunctionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.PaddingMedium, Dimens.PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(category.items, key = { it.id }) { item ->
            AnimatedListItem(category.items.indexOf(item)) {
                SwitchActionCard(
                    title = item.name,
                    subtitle = item.description,
                    isChecked = item.isEnabled,
                    onCheckedChange = { onFunctionToggle(item.id, it) },
                    isAvailable = item.isAvailable,
                    onClick = if (item.isClickable) { { onFunctionClick(item.id) } } else null
                )
            }
        }
    }
}

@Composable
private fun UpdateLogDialogContent(
    visible: Boolean,
    state: UpdateLogState,
    onDismiss: () -> Unit
) {
    CenterDialog(visible = visible, onDismiss = onDismiss) {
        BaseDialogSurface(title = "更新日志") {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(32.dp),
                            color = AccentBlue
                        )
                    }
                    state.error != null -> {
                        Text(
                            text = state.error,
                            fontSize = 14.sp,
                            color = QFunTheme.colors.textSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {
                        state.logs.forEach { (version, log) ->
                            Text(
                                text = version,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log,
                                fontSize = 14.sp,
                                color = QFunTheme.colors.textSecondary,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSection(
    versionInfo: String,
    onGithubClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onGroupClick: () -> Unit,
    onDonateClick: () -> Unit
) {
    val colors = QFunTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "关于",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialButton(
                R.drawable.ic_logo_github,
                "Github",
                onGithubClick
            )
            SocialButton(
                R.drawable.ic_logo_telegram,
                "Channel",
                onTelegramClick
            )
            SocialButton(
                R.drawable.ic_logo_qq,
                "Group",
                onGroupClick
            )
            SocialButton(
                R.drawable.ic_logo_donate,
                "Donate",
                onDonateClick
            )
        }
        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
        Text(
            text = versionInfo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun ConfigDialogOverlay(activeConfigKey: String?, onDismiss: () -> Unit) {
    val configUi = activeConfigKey?.let { ConfigUiRegistry.getConfigUi(it) }
    CenterDialog(visible = activeConfigKey != null && configUi != null, onDismiss = onDismiss) {
        configUi?.invoke(onDismiss)
    }
}