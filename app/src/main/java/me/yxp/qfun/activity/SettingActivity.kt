package me.yxp.qfun.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.hook.chat.RepeatMsg
import me.yxp.qfun.ui.components.dialogs.ConfirmDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.settings.SettingsScreen
import me.yxp.qfun.ui.viewmodel.SettingViewModel
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class SettingActivity : BaseComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainHook.initAllConfigUI()
        setContent {
            val vm: SettingViewModel = viewModel()
            val appMetrics = applicationContext.resources.displayMetrics
            val stableDensity = Density(
                density = appMetrics.density,
                fontScale = appMetrics.scaledDensity / appMetrics.density
            )
            CompositionLocalProvider(LocalDensity provides stableDensity) {
                QFunTheme(isDarkTheme) {
                    SettingsScreen(
                        categories = vm.categories,
                        versionInfo = vm.buildVersionInfo(),
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = ::toggleTheme,
                        onImportConfig = ::startImportConfig,
                        onExportConfig = ::startExportConfig,
                        onFunctionToggle = vm::toggleFunction,
                        onFunctionClick = vm::handleFunctionClick,
                        onGithubClick = { openUrl("https://github.com/oneQAQone/QFun") },
                        onTelegramClick = { openUrl("https://t.me/QFunChannel") },
                        onGroupClick = { openUrl("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1067198087&card_type=group&source=qrcode") },
                        onDonateClick = vm::showDonate,
                        activeConfigKey = vm.activeConfigKey,
                        onDismissDialog = vm::dismissDialog,
                        showUpdateLogDialog = vm.showUpdateLogDialog,
                        updateLogState = vm.updateLogState,
                        onUpdateLogClick = vm::showUpdateLog,
                        onDismissUpdateLog = vm::dismissUpdateLog
                    )
                    ConfirmDialog(
                        visible = vm.showDonateDialog,
                        title = "支持作者",
                        message = "如果您觉得这个模块好用，可以请作者喝一杯咖啡☕\n您的支持是我更新的动力！",
                        confirmText = "前往支持",
                        dismissText = "下次一定",
                        onDismiss = vm::dismissDonate,
                        onConfirm = {
                            vm.dismissDonate()
                            openUrl("http://127.0.0.1/")
                        }
                    )
                    vm.updateInfo?.let { info ->
                        ConfirmDialog(
                            visible = vm.showUpdateDialog,
                            title = "${info.releaseDate} ${info.latestVersion}",
                            message = info.releaseNotes,
                            confirmText = "立即更新",
                            dismissText = "稍后再说",
                            onDismiss = vm::ignoreUpdate,
                            onConfirm = {
                                vm.dismissUpdateDialog()
                                openUrl(info.downloadUrl)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        MainHook.processDataForCurrent("save")
        super.onDestroy()
    }

    private fun startExportConfig() {
        val fileName = "QFun_Backup_${QQCurrentEnv.currentUin}_${
            SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
        }.zip"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, REQUEST_CODE_EXPORT)
    }

    private fun startImportConfig() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, REQUEST_CODE_IMPORT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return
        val uri = data.data ?: return
        when (requestCode) {
            REQUEST_CODE_EXPORT -> performExport(uri)
            REQUEST_CODE_IMPORT -> performImport(uri)
            REQUEST_CODE_IMAGE -> handleImagePick(uri)
        }
    }

    private fun handleImagePick(uri: Uri) {
        runCatching {
            RepeatMsg.bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            Toasts.qqToast(2, "加一图标导入成功")
        }.onFailure { Toasts.qqToast(1, "加一图标导入失败") }
    }

    private fun performExport(uri: Uri) {
        val vm = ViewModelProvider(this)[SettingViewModel::class.java]
        vm.performExport(this, uri)
    }

    private fun performImport(uri: Uri) {
        val vm = ViewModelProvider(this)[SettingViewModel::class.java]
        vm.performImport(this, uri)
    }

    private fun openUrl(url: String) =
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }

    companion object {
        private const val REQUEST_CODE_EXPORT = 1001
        private const val REQUEST_CODE_IMPORT = 1002
        const val REQUEST_CODE_IMAGE = 1003
    }
}
