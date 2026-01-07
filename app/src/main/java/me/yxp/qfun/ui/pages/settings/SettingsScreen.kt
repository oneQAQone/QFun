package me.yxp.qfun.ui.pages.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.width
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
import me.yxp.qfun.ui.components.atoms.ConfigButton
import me.yxp.qfun.ui.components.atoms.SocialButton
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.listitems.SwitchActionCard
import me.yxp.qfun.ui.components.molecules.AnimatedListItem
import me.yxp.qfun.ui.components.molecules.QFunTopBar
import me.yxp.qfun.ui.components.molecules.TopBarIconButton
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.configs.ConfigUiRegistry
import me.yxp.qfun.ui.pages.settings.components.CategoryCard
import me.yxp.qfun.ui.viewmodel.UpdateLogState

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
    versionInfo: String,
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
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val mainListState = rememberLazyListState()

    BackHandler(activeConfigKey != null, onDismissDialog)
    BackHandler(activeConfigKey == null && selectedCategoryName != null) {
        selectedCategoryName = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            AnimatedContent(
                targetState = selectedCategoryName,
                transitionSpec = {
                    if (targetState != null) {
                        slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
                    } else {
                        slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
                    }
                },
                label = "pageTransition"
            ) { categoryName ->
                if (categoryName == null) {
                    MainPage(
                        categories = categories,
                        versionInfo = versionInfo,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        onUpdateLogClick = onUpdateLogClick,
                        onImportConfig = onImportConfig,
                        onExportConfig = onExportConfig,
                        onCategoryClick = { selectedCategoryName = it.name },
                        onGithubClick = onGithubClick,
                        onTelegramClick = onTelegramClick,
                        onGroupClick = onGroupClick,
                        onDonateClick = onDonateClick,
                        listState = mainListState
                    )
                } else {
                    categories.find { it.name == categoryName }?.let { category ->
                        DetailPage(
                            category = category,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle,
                            onBackClick = { selectedCategoryName = null },
                            onFunctionToggle = onFunctionToggle,
                            onFunctionClick = onFunctionClick
                        )
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
private fun MainPage(
    categories: List<CategoryData>,
    versionInfo: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onUpdateLogClick: () -> Unit,
    onImportConfig: () -> Unit,
    onExportConfig: () -> Unit,
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
        item {
            QFunTopBar(
                title = "QFun",
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                actions = {
                    TopBarIconButton(R.drawable.ic_update_log, "更新日志", onUpdateLogClick)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            )
        }
        item { ConfigButtonRow(onImportConfig, onExportConfig) }
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
private fun ConfigButtonRow(onImportConfig: () -> Unit, onExportConfig: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingLarge, Dimens.PaddingSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        ConfigButton("导入配置", onImportConfig, Modifier.weight(1f))
        ConfigButton("导出配置", onExportConfig, Modifier.weight(1f))
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
            "关于",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)) {
            SocialButton(R.drawable.ic_logo_github, "Github", onGithubClick)
            SocialButton(R.drawable.ic_logo_telegram, "Channel", onTelegramClick)
            SocialButton(R.drawable.ic_logo_qq, "Group", onGroupClick)
            SocialButton(R.drawable.ic_logo_donate, "Donate", onDonateClick)
        }
        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
        Text(
            versionInfo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun DetailPage(
    category: CategoryData,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBackClick: () -> Unit,
    onFunctionToggle: (String, Boolean) -> Unit,
    onFunctionClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        QFunTopBar(
            category.name,
            showBackButton = true,
            onBackClick = onBackClick,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.PaddingMedium, Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(category.items, key = { it.id }) { item ->
                AnimatedListItem(category.items.indexOf(item)) {
                    SwitchActionCard(
                        title = item.name,
                        isChecked = item.isEnabled,
                        onCheckedChange = { onFunctionToggle(item.id, it) },
                        subtitle = item.description,
                        isAvailable = item.isAvailable,
                        onClick = if (item.isClickable) {
                            { onFunctionClick(item.id) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigDialogOverlay(activeConfigKey: String?, onDismiss: () -> Unit) {
    val configUi = activeConfigKey?.let { ConfigUiRegistry.getConfigUi(it) }
    CenterDialog(activeConfigKey != null && configUi != null, onDismiss) {
        configUi?.invoke(onDismiss)
    }
}
