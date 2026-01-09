package me.yxp.qfun.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import me.yxp.qfun.ui.components.dialogs.ConfirmDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.plugin.PluginScreen
import me.yxp.qfun.ui.viewmodel.PluginViewModel
import me.yxp.qfun.utils.net.HttpUtils

@Suppress("DEPRECATION")
class PluginActivity : BaseComposeActivity() {

    companion object {
        const val REQUEST_CODE_PICK_ICON = 1004
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: PluginViewModel = viewModel()
            val appMetrics = applicationContext.resources.displayMetrics
            val stableDensity = Density(
                density = appMetrics.density,
                fontScale = appMetrics.scaledDensity / appMetrics.density
            )
            CompositionLocalProvider(LocalDensity provides stableDensity) {
                QFunTheme(isDarkTheme) {
                    PluginScreen(
                        localPlugins = vm.localPlugins,
                        onlineUiState = vm.onlineUiState,
                        downloadingPlugins = vm.downloadingPlugins,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = ::toggleTheme,
                        onBackClick = ::finish,
                        onCreatePlugin = vm::createPlugin,
                        onDocsClick = ::openDocs,
                        onRefreshOnline = vm::fetchOnlinePlugins,
                        onPluginRunToggle = vm::togglePluginRun,
                        onPluginAutoLoadToggle = vm::togglePluginAutoLoad,
                        onPluginDelete = vm::showDeleteConfirm,
                        onPluginReload = vm::reloadPlugin,
                        onPluginUpload = vm::showUploadConfirm,
                        onPluginDownload = vm::downloadPlugin,
                        onPickIcon = ::openPickIcon
                    )
                    ConfirmDialog(
                        visible = vm.showDeleteDialog,
                        title = "删除脚本",
                        message = "确定要删除此脚本吗？",
                        confirmText = "确定",
                        dismissText = "取消",
                        onDismiss = vm::dismissDeleteDialog,
                        onConfirm = vm::confirmDelete
                    )
                    ConfirmDialog(
                        visible = vm.showUploadDialog,
                        title = "上传脚本",
                        message = "确定要上传此脚本到在线脚本库吗？\n上传后其他用户可以下载使用。",
                        confirmText = "确定上传",
                        dismissText = "取消",
                        onDismiss = vm::dismissUploadDialog,
                        onConfirm = vm::confirmUpload
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return
        val uri = data.data ?: return
        if (requestCode == REQUEST_CODE_PICK_ICON) {
            val vm = ViewModelProvider(this)[PluginViewModel::class.java]
            vm.handleIconSelection(this, uri)
        }
    }

    private fun openPickIcon() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_ICON)
    }

    private fun openDocs() = runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, "${HttpUtils.HOST}/doc.php".toUri()))
    }
}