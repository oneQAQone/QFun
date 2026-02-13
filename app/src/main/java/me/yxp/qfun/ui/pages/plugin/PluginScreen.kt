package me.yxp.qfun.ui.pages.plugin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.DialogButton
import me.yxp.qfun.ui.components.atoms.DialogTextField
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.dialogs.CenterDialog
import me.yxp.qfun.ui.components.molecules.QFunTopBar
import me.yxp.qfun.ui.components.molecules.TabItem
import me.yxp.qfun.ui.components.molecules.TopBarIconButton
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