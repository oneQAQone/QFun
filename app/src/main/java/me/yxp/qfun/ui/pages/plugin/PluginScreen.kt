package me.yxp.qfun.ui.pages.plugin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.*
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.molecules.*
import me.yxp.qfun.ui.core.theme.AccentGreen
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.viewmodel.PluginListUiState
import me.yxp.qfun.utils.qq.Toasts

@Immutable
data class LocalPluginData(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isRunning: Boolean,
    val isAutoLoad: Boolean,
    val createdAt: Long = 0L,
    val modifiedAt: Long = 0L
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

enum class PluginSortType {
    NAME_A_TO_Z,
    CREATED_TIME,
    MODIFIED_TIME
}

@Composable
fun PluginScreen(
    localPlugins: List<LocalPluginData>,
    onlineUiState: PluginListUiState,
    downloadingPlugins: Set<String>,
    isLocalRefreshing: Boolean,
    isOnlineRefreshing: Boolean,
    themeMode: Int,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBackClick: () -> Unit,
    onCreatePlugin: () -> Unit,
    onDocsClick: () -> Unit,
    onRefreshLocal: () -> Unit,
    onRefreshOnline: () -> Unit,
    onPluginRunToggle: (String, Boolean) -> Unit,
    onPluginAutoLoadToggle: (String, Boolean) -> Unit,
    onPluginDelete: (String) -> Unit,
    onPluginReload: (String) -> Unit,
    onPluginUpload: (String) -> Unit,
    onPluginDownload: (String) -> Unit,
    onPickIcon: () -> Unit,
    showCreateDialog: Boolean,
    onDismissCreateDialog: () -> Unit,
    onConfirmCreatePlugin: (String, String, String, String) -> Unit,
    showSuccessDialog: Boolean,
    createdPluginPath: String,
    onDismissSuccessDialog: () -> Unit
) {
    val colors = QFunTheme.colors
    var selectedTab by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("QFun_Config_global", Context.MODE_PRIVATE) }
    val KEY_SORT_TYPE = "plugin_sort_type"

    val savedSortType = remember {
        prefs.getString(KEY_SORT_TYPE, PluginSortType.NAME_A_TO_Z.name) ?: PluginSortType.NAME_A_TO_Z.name
    }
    var sortType by remember { mutableStateOf(PluginSortType.valueOf(savedSortType)) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(sortType) {
        prefs.edit().putString(KEY_SORT_TYPE, sortType.name).apply()
    }

    val sortedLocalPlugins = remember(localPlugins, sortType) {
        when (sortType) {
            PluginSortType.NAME_A_TO_Z -> localPlugins.sortedBy { it.name.lowercase() }
            PluginSortType.CREATED_TIME -> localPlugins.sortedByDescending { it.createdAt }
            PluginSortType.MODIFIED_TIME -> localPlugins.sortedByDescending { it.modifiedAt }
        }
    }

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
            themeMode = themeMode,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            actions = {
                TopBarCapsuleButton(
                    iconRes = R.drawable.ic_settings_gear,
                    label = "设置图标",
                    onClick = onPickIcon
                )
            }
        )

        PluginTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onCreatePlugin = onCreatePlugin,
            onDocsClick = onDocsClick,
            onLongPressLocalTab = { showSortMenu = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> LocalPluginPage(
                plugins = sortedLocalPlugins,
                isRefreshing = isLocalRefreshing,
                onRunToggle = onPluginRunToggle,
                onAutoLoadToggle = onPluginAutoLoadToggle,
                onDelete = onPluginDelete,
                onReload = onPluginReload,
                onUpload = onPluginUpload,
                onRefresh = onRefreshLocal
            )
            1 -> OnlinePluginPage(
                uiState = onlineUiState,
                isOnlineRefreshing = isOnlineRefreshing,
                downloadingPlugins = downloadingPlugins,
                onDownload = onPluginDownload,
                onRefresh = onRefreshOnline
            )
        }
    }

    CreatePluginDialog(
        visible = showCreateDialog,
        onDismiss = onDismissCreateDialog,
        onConfirm = onConfirmCreatePlugin
    )

    PluginCreatedDialog(
        visible = showSuccessDialog,
        path = createdPluginPath,
        onDismiss = onDismissSuccessDialog
    )

    SortMenuDialog(
        visible = showSortMenu,
        currentSortType = sortType,
        onDismiss = { showSortMenu = false },
        onSortSelected = { sortType = it; showSortMenu = false }
    )
}

@Composable
private fun PluginTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onCreatePlugin: () -> Unit,
    onDocsClick: () -> Unit,
    onLongPressLocalTab: () -> Unit
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
            // 本地脚本：支持长按
            Text(
                text = "本地脚本",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 0) QFunTheme.colors.accentGreen else QFunTheme.colors.textSecondary,
                modifier = Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(0) },
                        onLongClick = onLongPressLocalTab
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            // 在线脚本
            Text(
                text = "在线脚本",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == 1) QFunTheme.colors.accentGreen else QFunTheme.colors.textSecondary,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(1) }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            // 创建脚本
            Text(
                text = "创建脚本",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = QFunTheme.colors.textSecondary,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCreatePlugin
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            // 开发文档
            Text(
                text = "开发文档",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = QFunTheme.colors.textSecondary,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDocsClick
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun SortMenuDialog(
    visible: Boolean,
    currentSortType: PluginSortType,
    onDismiss: () -> Unit,
    onSortSelected: (PluginSortType) -> Unit
) {
    if (!visible) return

    CenterDialog(visible = visible, onDismiss = onDismiss) {
        BaseDialogSurface(
            title = "排序方式",
            bottomBar = {
                DialogButton("取消", onDismiss, false)
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOptionItem(
                    title = "按名称 A-Z",
                    isSelected = currentSortType == PluginSortType.NAME_A_TO_Z,
                    onClick = { onSortSelected(PluginSortType.NAME_A_TO_Z) }
                )
                SortOptionItem(
                    title = "按创建时间（新到旧）",
                    isSelected = currentSortType == PluginSortType.CREATED_TIME,
                    onClick = { onSortSelected(PluginSortType.CREATED_TIME) }
                )
                SortOptionItem(
                    title = "按修改时间（新到旧）",
                    isSelected = currentSortType == PluginSortType.MODIFIED_TIME,
                    onClick = { onSortSelected(PluginSortType.MODIFIED_TIME) }
                )
            }
        }
    }
}

@Composable
private fun SortOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = QFunTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = if (isSelected) colors.accentGreen else colors.textPrimary
        )
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_logo_check),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.accentGreen
            )
        }
    }
}

@Composable
internal fun ScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.75f),
        exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.75f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = CircleShape, clip = false)
                .clip(CircleShape)
                .background(QFunTheme.colors.cardBackground)
                .size(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                contentDescription = null,
                tint = QFunTheme.colors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CreatePluginDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (id: String, name: String, version: String, author: String) -> Unit
) {
    if (!visible) return

    val timestamp = System.currentTimeMillis()
    var id by remember { mutableStateOf("example_$timestamp") }
    var name by remember { mutableStateOf("示例脚本") }
    var version by remember { mutableStateOf("1.0") }
    var author by remember { mutableStateOf("QFunDeveloper") }

    CenterDialog(visible = visible, onDismiss = onDismiss) {
        BaseDialogSurface(
            title = "创建脚本",
            bottomBar = {
                DialogButton("取消", onDismiss, false)
                Spacer(modifier = Modifier.width(12.dp))
                DialogButton("创建", {
                    onConfirm(id, name, version, author)
                }, true)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                DialogTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = "脚本ID",
                    hint = "唯一标识，不可重复",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DialogTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "脚本名称 (文件夹名)",
                    hint = "不可与现有文件夹重复",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DialogTextField(
                    value = version,
                    onValueChange = { version = it },
                    label = "版本号",
                    hint = "1.0",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DialogTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = "作者",
                    hint = "作者名称",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }
    }
}

@Composable
private fun PluginCreatedDialog(
    visible: Boolean,
    path: String,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val context = LocalContext.current
    val colors = QFunTheme.colors

    CenterDialog(visible = visible, onDismiss = onDismiss) {
        BaseDialogSurface(
            title = "创建成功",
            bottomBar = {
                DialogButton("关闭", onDismiss, false)
            }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_logo_check),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = AccentGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "脚本文件夹已创建",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))
                QFunCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Plugin Path", path)
                            clipboard.setPrimaryClip(clip)
                            Toasts.qqToast(2, "路径已复制")
                        },
                    animateContentSize = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.PaddingMedium)
                    ) {
                        Text(
                            text = "存储路径 (点击复制)",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = path,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = colors.textPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
