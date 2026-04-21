package me.yxp.qfun.ui.pages.plugin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.DialogButton
import me.yxp.qfun.ui.components.atoms.DialogTextField
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.molecules.FloatingLiquidTabs
import me.yxp.qfun.ui.components.molecules.SearchTopBar
import me.yxp.qfun.ui.components.molecules.TopBarMenuItem
import me.yxp.qfun.ui.core.theme.AccentGreen
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
    onDismissSuccessDialog: () -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    val colors = QFunTheme.colors
    
    var selectedTab by remember { 
        mutableIntStateOf(value = 0) 
    }

    val localListState = rememberLazyListState()
    
    val onlineListState = rememberLazyListState()
    
    val isAnyRefreshing = isLocalRefreshing || isOnlineRefreshing

    var isBottomBarVisible by remember { 
        mutableStateOf(value = true) 
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset, 
                source: NestedScrollSource
            ): Offset {
                val delta = available.y

                if (delta < -15f) {
                    isBottomBarVisible = false
                }
                
                return Offset.Zero
            }
        }
    }

    val animatedOffsetDp by animateDpAsState(
        targetValue = if (isBottomBarVisible || isAnyRefreshing) {
            0.dp
        } else {
            120.dp
        },
        animationSpec = tween(
            durationMillis = 350
        ),
        label = "BottomBarMoveAnim"
    )

    BackHandler(
        enabled = isSearchActive
    ) { 
        onSearchActiveChange(false) 
    }

    val screenBackdrop = rememberLayerBackdrop()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colors.background
            )
            .pointerInput(key1 = Unit) {
                awaitEachGesture {

                    awaitPointerEvent(
                        pass = PointerEventPass.Initial
                    )
                    isBottomBarVisible = true
                }
            }
            .nestedScroll(
                connection = nestedScrollConnection
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    insets = WindowInsets.statusBars
                )
        ) {
            SearchTopBar(
                title = "Java Plugin",
                searchQuery = searchQuery,
                onQueryChange = onQueryChange,
                isSearchActive = isSearchActive,
                onSearchActiveChange = onSearchActiveChange,
                showBackButton = true,
                onBackClick = onBackClick,
                themeMode = themeMode,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                searchHint = "搜索脚本...",
                menuItems = listOf(
                    TopBarMenuItem(
                        title = "创建脚本", 
                        iconRes = R.drawable.ic_add_circle, 
                        onClick = onCreatePlugin
                    ),
                    TopBarMenuItem(
                        title = "设置图标", 
                        iconRes = R.drawable.ic_settings_gear, 
                        onClick = onPickIcon
                    ),
                    TopBarMenuItem(
                        title = "开发文档", 
                        iconRes = R.drawable.ic_update_log, 
                        onClick = onDocsClick
                    )
                )
            )

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { 
                    fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ) togetherWith fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    ) 
                },
                modifier = Modifier
                    .weight(1f)
                    .layerBackdrop(screenBackdrop),
                label = "PluginTabContent"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> LocalPluginPage(
                        plugins = localPlugins,
                        isRefreshing = isLocalRefreshing,
                        searchQuery = searchQuery,
                        listState = localListState,
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
                        searchQuery = searchQuery,
                        listState = onlineListState,
                        downloadingPlugins = downloadingPlugins,
                        onDownload = onPluginDownload,
                        onRefresh = onRefreshOnline
                    )
                }
            }
        }

        FloatingLiquidTabs(
            options = listOf("本地脚本", "在线脚本"),
            selectedIndex = selectedTab,
            onOptionSelected = { 
                selectedTab = it
                isBottomBarVisible = true
            },
            backdrop = screenBackdrop,
            modifier = Modifier
                .align(
                    alignment = Alignment.BottomCenter
                )
                .offset { 
                    IntOffset(
                        x = 0, 
                        y = animatedOffsetDp.roundToPx()
                    ) 
                }
                .padding(
                    bottom = 28.dp
                )
                .navigationBarsPadding()
        )
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
}

@Composable
internal fun ScrollToTopButton(
    visible: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.8f
        ),
        exit = fadeOut() + scaleOut(
            targetScale = 0.8f
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 8.dp, 
                    shape = CircleShape
                )
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = QFunTheme.colors.cardBackground
                )
                .size(
                    size = 48.dp
                )
                .clickable(
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_arrow_up
                ), 
                contentDescription = null, 
                modifier = Modifier.size(
                    size = 22.dp
                ),
                tint = QFunTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun CreatePluginDialog(
    visible: Boolean, 
    onDismiss: () -> Unit, 
    onConfirm: (String, String, String, String) -> Unit
) {
    if (!visible) return
    
    var id by remember { 
        mutableStateOf(value = "example_${System.currentTimeMillis()}") 
    }
    
    var name by remember { 
        mutableStateOf(value = "示例脚本") 
    }
    
    var version by remember { 
        mutableStateOf(value = "1.0") 
    }
    
    var author by remember { 
        mutableStateOf(value = "QFunDeveloper") 
    }

    CenterDialog(
        visible = visible, 
        onDismiss = onDismiss
    ) {
        BaseDialogSurface(
            title = "创建脚本", 
            bottomBar = {
                DialogButton(
                    text = "取消", 
                    onClick = onDismiss, 
                    isPrimary = false
                )
                
                Spacer(
                    modifier = Modifier.width(
                        width = 12.dp
                    )
                )
                
                DialogButton(
                    text = "创建", 
                    onClick = { 
                        onConfirm(id, name, version, author) 
                    }, 
                    isPrimary = true
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState()
                    )
            ) {
                DialogTextField(
                    value = id, 
                    onValueChange = { id = it }, 
                    label = "脚本ID", 
                    hint = "唯一标识"
                )
                
                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )
                
                DialogTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = "脚本名称", 
                    hint = "文件夹名"
                )
                
                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )
                
                DialogTextField(
                    value = version, 
                    onValueChange = { version = it }, 
                    label = "版本号", 
                    hint = "1.0"
                )
                
                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )
                
                DialogTextField(
                    value = author, 
                    onValueChange = { author = it }, 
                    label = "作者", 
                    hint = "作者名称"
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
    
    CenterDialog(
        visible = visible, 
        onDismiss = onDismiss
    ) {
        BaseDialogSurface(
            title = "创建成功", 
            bottomBar = { 
                DialogButton(
                    text = "关闭", 
                    onClick = onDismiss, 
                    isPrimary = false
                ) 
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_logo_check
                    ), 
                    contentDescription = null, 
                    modifier = Modifier.size(
                        size = 60.dp
                    ), 
                    tint = AccentGreen
                )
                
                Spacer(
                    modifier = Modifier.height(
                        height = 16.dp
                    )
                )
                
                Text(
                    text = "脚本文件夹已创建", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp, 
                    color = QFunTheme.colors.textPrimary
                )
                
                Spacer(
                    modifier = Modifier.height(
                        height = 24.dp
                    )
                )
                
                QFunCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Plugin Path", 
                                    path
                                )
                            )
                            Toasts.qqToast(2, "路径已复制")
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                all = 16.dp
                            )
                    ) {
                        Text(
                            text = "存储路径 (点击复制)", 
                            fontSize = 12.sp, 
                            color = QFunTheme.colors.textSecondary
                        )
                        
                        Spacer(
                            modifier = Modifier.height(
                                height = 4.dp
                            )
                        )
                        
                        Text(
                            text = path, 
                            fontSize = 13.sp, 
                            fontFamily = FontFamily.Monospace, 
                            color = QFunTheme.colors.textPrimary, 
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}