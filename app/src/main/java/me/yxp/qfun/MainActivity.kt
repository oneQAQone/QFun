package me.yxp.qfun

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import me.yxp.qfun.ui.components.dialogs.ConfirmDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.home.MainScreen
import me.yxp.qfun.utils.hook.hookstatus.HookStatus
import me.yxp.qfun.utils.qq.HostInfo

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    private var isActivated by mutableStateOf(false)
    private var frameworkInfo by mutableStateOf("")
    private var isIconVisible by mutableStateOf(true)
    private var showHideConfirmDialog by mutableStateOf(false)

    private val launcherComponent by lazy { ComponentName(this, LauncherActivity::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        HookStatus.init(this)
        updateActivationStatus()
        updateIconState()

        setContent {
            QFunTheme(false) {
                MainScreen(
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    isActivated,
                    frameworkInfo,
                    isIconVisible,
                    { handleToggleIcon() },
                    { jump("https://t.me/QFunChannel") },
                    { jump("https://github.com/oneQAQone/QFun") },
                    { jump("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1067198087&card_type=group&source=qrcode") }
                )

                ConfirmDialog(
                    showHideConfirmDialog,
                    "隐藏图标",
                    "确定要隐藏桌面图标吗？\n\n隐藏后您需要通过模块管理器打开设置。\n\n应用将会重启以刷新状态。",
                    "确定",
                    "取消",
                    { showHideConfirmDialog = false },
                    { showHideConfirmDialog = false; toggleIcon(false) }
                )
            }
        }
    }

    private fun updateIconState() {
        val state = packageManager.getComponentEnabledSetting(launcherComponent)
        isIconVisible = state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    }

    private fun handleToggleIcon() {
        if (isIconVisible) showHideConfirmDialog = true else toggleIcon(true)
    }

    private fun toggleIcon(enable: Boolean) {
        val newState =
            if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        try {
            packageManager.setComponentEnabledSetting(launcherComponent, newState, 0)
            Toast.makeText(this, if (enable) "图标已恢复" else "图标已隐藏", Toast.LENGTH_LONG)
                .show()
            isIconVisible = enable
        } catch (_: Exception) {
        }
    }

    private fun jump(url: String) = startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))

    private fun updateActivationStatus() {
        isActivated = checkHookStatus()
        updateFrameworkInfo()
    }

    private fun checkHookStatus(): Boolean {
        val isEnabledByLegacyApi = HookStatus.isModuleEnabled() || HostInfo.isInHostProcess
        val isEnabledByLibXposedApi = checkLibXposedStatus()
        return isEnabledByLegacyApi || isEnabledByLibXposedApi
    }

    private fun checkLibXposedStatus(): Boolean {
        val xposedService = HookStatus.getXposedService().value ?: return false
        val targetScope = setOf(HostInfo.PACKAGE_NAME_QQ, HostInfo.PACKAGE_NAME_TIM)
        return xposedService.scope.intersect(targetScope).isNotEmpty()
    }

    private fun updateFrameworkInfo() {
        if (!isActivated) {
            frameworkInfo = HookStatus.getHookProviderNameForLegacyApi()
            return
        }
        val xposedService = HookStatus.getXposedService().value
        frameworkInfo = if (xposedService != null) {
            "${xposedService.frameworkName} ${xposedService.frameworkVersion} (${xposedService.frameworkVersionCode}), API ${xposedService.apiVersion}"
        } else HookStatus.getHookProviderNameForLegacyApi()
    }
}
