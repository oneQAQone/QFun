package me.yxp.qfun.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.settings.CategoryData
import me.yxp.qfun.ui.pages.settings.FunctionData
import me.yxp.qfun.utils.io.BackupManager
import me.yxp.qfun.utils.net.HttpUtils
import me.yxp.qfun.utils.net.UpdateManager
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.Toasts
import org.json.JSONObject

data class UpdateLogState(
    val isLoading: Boolean = false,
    val logs: List<Pair<String, String>> = emptyList(),
    val error: String? = null
)

class SettingViewModel : ViewModel() {

    var categories by mutableStateOf<List<CategoryData>>(emptyList())
        private set

    var showDonateDialog by mutableStateOf(false)
        private set

    var showUpdateDialog by mutableStateOf(false)
        private set

    var updateInfo by mutableStateOf<UpdateManager.UpdateInfo?>(null)
        private set

    var activeConfigKey by mutableStateOf<String?>(null)
        private set

    var showUpdateLogDialog by mutableStateOf(false)
        private set

    var updateLogState by mutableStateOf(UpdateLogState())
        private set

    private val allHookItems = MainHook.switchHookItemList

    companion object {
        private var ignoredVersion: String? = null
    }

    init {
        refreshCategories()
        checkUpdate()
    }

    private fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            UpdateManager.checkUpdateSuspend()?.let { info ->
                if (info.latestVersion != ignoredVersion) {
                    updateInfo = info
                    showUpdateDialog = true
                }
            }
        }
    }

    fun dismissUpdateDialog() {
        showUpdateDialog = false
    }

    fun ignoreUpdate() {
        showUpdateDialog = false
        updateInfo?.let { ignoredVersion = it.latestVersion }
    }

    fun buildVersionInfo(): String {
        return "模块版本：V${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) ${HostInfo.hostName}版本：V${HostInfo.versionName}(${HostInfo.versionCode})"
    }

    fun refreshCategories() {
        categories = HookCategory.ORDER.mapNotNull { category ->
            val itemsInCategory = allHookItems.filter { it.category == category }
            if (itemsInCategory.isNotEmpty()) {
                CategoryData(category, itemsInCategory.map { hookItem ->
                    val isClickable = hookItem is BaseClickableHookItem<*>
                    FunctionData(
                        hookItem.name,
                        hookItem.tag,
                        hookItem.desc,
                        hookItem.isEnable,
                        hookItem.isAvailable,
                        isClickable,
                        if (isClickable) hookItem.name else null
                    )
                })
            } else null
        }
    }

    fun toggleFunction(id: String, enabled: Boolean) {
        allHookItems.find { it.name == id }?.let { item ->
            item.isEnable = enabled
            refreshCategories()
        }
    }

    fun handleFunctionClick(id: String) {
        allHookItems.find { it.name == id }?.let { item ->
            if (item is BaseClickableHookItem<*>) {
                item.initData()
                activeConfigKey = item.name
            }
        }
    }

    fun dismissDialog() {
        activeConfigKey = null
    }

    fun showDonate() {
        showDonateDialog = true
    }

    fun dismissDonate() {
        showDonateDialog = false
    }

    fun performExport(context: Context, uri: Uri) {
        viewModelScope.launch {
            BackupManager.performExport(context, uri).fold(
                onSuccess = { Toasts.qqToast(2, "备份导出成功") },
                onFailure = { Toasts.qqToast(1, "导出失败: ${it.message}") }
            )
        }
    }

    fun performImport(context: Context, uri: Uri) {
        viewModelScope.launch {
            BackupManager.performImport(context, uri).fold(
                onSuccess = {
                    refreshCategories()
                    Toasts.qqToast(2, "配置导入成功")
                },
                onFailure = { Toasts.qqToast(1, "导入失败: ${it.message}") }
            )
        }
    }

    fun showUpdateLog() {
        showUpdateLogDialog = true
        loadUpdateLog()
    }

    fun dismissUpdateLog() {
        showUpdateLogDialog = false
    }

    private fun loadUpdateLog() {
        if (updateLogState.logs.isNotEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            updateLogState = UpdateLogState(isLoading = true)
            val response = HttpUtils.getSuspend("${HttpUtils.HOST}/api/updatelog.php")
            updateLogState = if (response.isNotEmpty()) {
                try {
                    val json = JSONObject(response)
                    val logs = mutableListOf<Pair<String, String>>()
                    json.keys().forEach { key -> logs.add(key to json.getString(key)) }
                    logs.sortByDescending { it.first }
                    UpdateLogState(logs = logs)
                } catch (_: Exception) {
                    UpdateLogState(error = "解析数据失败")
                }
            } else {
                UpdateLogState(error = "获取数据失败")
            }
        }
    }
}
