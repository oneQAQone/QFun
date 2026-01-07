package me.yxp.qfun.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.yxp.qfun.plugin.loader.PluginManager
import me.yxp.qfun.plugin.net.ScriptInfo
import me.yxp.qfun.plugin.net.ScriptService
import me.yxp.qfun.ui.pages.plugin.LocalPluginData
import me.yxp.qfun.ui.pages.plugin.OnlinePluginData
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import java.io.File

sealed interface PluginListUiState {
    data object Loading : PluginListUiState
    data class Success(val data: List<OnlinePluginData>) : PluginListUiState
    data class Error(val message: String) : PluginListUiState
}

class PluginViewModel : ViewModel() {

    var localPlugins by mutableStateOf<List<LocalPluginData>>(emptyList())
        private set

    var onlineUiState by mutableStateOf<PluginListUiState>(PluginListUiState.Loading)
        private set

    var downloadingPlugins by mutableStateOf<Set<String>>(emptySet())
        private set

    var showDeleteDialog by mutableStateOf(false)
        private set

    var showUploadDialog by mutableStateOf(false)
        private set

    var pendingDeletePluginId by mutableStateOf<String?>(null)
        private set

    var pendingUploadPluginId by mutableStateOf<String?>(null)
        private set

    private val scriptList = mutableListOf<ScriptInfo>()

    init {
        refreshLocalPlugins()
        PluginManager.loadAll()
        fetchOnlinePlugins()
    }

    fun refreshLocalPlugins() {
        localPlugins = PluginManager.plugins.map { plugin ->
            LocalPluginData(
                plugin.id,
                plugin.name,
                plugin.version,
                plugin.author,
                plugin.desc,
                plugin.isRunning,
                PluginManager.autoLoadList.contains(plugin.id)
            )
        }
    }

    fun createPlugin(): Boolean {
        return if (PluginManager.createExamplePlugin()) {
            Toasts.qqToast(2, "创建成功")
            refreshLocalPlugins()
            true
        } else {
            Toasts.qqToast(1, "创建失败")
            false
        }
    }

    fun togglePluginRun(id: String, run: Boolean) {
        val plugin = PluginManager.plugins.find { it.id == id } ?: return
        if (run) {
            if (PluginManager.startPlugin(plugin)) refreshLocalPlugins()
        } else {
            PluginManager.stopPlugin(plugin)
            refreshLocalPlugins()
        }
    }

    fun togglePluginAutoLoad(id: String, autoLoad: Boolean) {
        val plugin = PluginManager.plugins.find { it.id == id } ?: return
        PluginManager.setAutoLoad(plugin, autoLoad)
        refreshLocalPlugins()
    }

    fun reloadPlugin(id: String) {
        val plugin = PluginManager.plugins.find { it.id == id } ?: return
        if (PluginManager.reloadPlugin(plugin)) {
            refreshLocalPlugins()
            Toasts.qqToast(2, "重载成功")
        }
    }

    fun showDeleteConfirm(id: String) {
        pendingDeletePluginId = id
        showDeleteDialog = true
    }

    fun dismissDeleteDialog() {
        showDeleteDialog = false
        pendingDeletePluginId = null
    }

    fun confirmDelete() {
        pendingDeletePluginId?.let { id ->
            PluginManager.plugins.find { it.id == id }?.let { plugin ->
                PluginManager.deletePlugin(plugin)
                refreshLocalPlugins()
            }
        }
        dismissDeleteDialog()
    }

    fun showUploadConfirm(id: String) {
        pendingUploadPluginId = id
        showUploadDialog = true
    }

    fun dismissUploadDialog() {
        showUploadDialog = false
        pendingUploadPluginId = null
    }

    fun confirmUpload() {
        val id = pendingUploadPluginId ?: return
        dismissUploadDialog()
        uploadPlugin(id)
    }

    private fun uploadPlugin(id: String) {
        val plugin = PluginManager.plugins.find { it.id == id } ?: return
        val scriptZip = File("${QQCurrentEnv.currentDir}cache", "${plugin.name}.zip")
        if (!FileUtils.zip(File(plugin.dirPath), scriptZip)) {
            Toasts.qqToast(1, "打包失败")
            return
        }
        viewModelScope.launch {
            val result = ScriptService.uploadScript(
                plugin.id,
                plugin.author,
                plugin.desc,
                plugin.version,
                plugin.name,
                scriptZip
            )
            FileUtils.delete(scriptZip)
            result.fold(
                onSuccess = { Toasts.qqToast(2, "上传成功: ${it.status}") },
                onFailure = { Toasts.qqToast(1, it.message ?: "上传失败") }
            )
        }
    }

    fun fetchOnlinePlugins() {
        if (onlineUiState is PluginListUiState.Loading && scriptList.isNotEmpty()) return

        onlineUiState = PluginListUiState.Loading

        viewModelScope.launch {
            ScriptService.fetchScriptList().fold(
                onSuccess = { list ->
                    scriptList.clear()
                    scriptList.addAll(list)
                    onlineUiState = PluginListUiState.Success(list.map { script ->
                        OnlinePluginData(
                            script.id,
                            script.name,
                            script.version,
                            script.author,
                            script.description,
                            script.downloads,
                            script.uploadTime
                        )
                    })
                },
                onFailure = { e ->
                    val errorMessage = e.message ?: "获取失败"
                    onlineUiState = PluginListUiState.Error(errorMessage)
                    Toasts.qqToast(1, errorMessage)
                }
            )
        }
    }

    fun downloadPlugin(id: String) {
        val script = scriptList.find { it.id == id } ?: return
        if (downloadingPlugins.contains(id)) return
        downloadingPlugins = downloadingPlugins + id
        Toasts.qqToast(0, "开始下载: ${script.name}")
        viewModelScope.launch {
            ScriptService.downloadAndInstall(script).fold(
                onSuccess = {
                    downloadingPlugins = downloadingPlugins - id
                    Toasts.qqToast(2, "安装成功")
                    refreshLocalPlugins()
                },
                onFailure = { e ->
                    downloadingPlugins = downloadingPlugins - id
                    Toasts.qqToast(1, e.message ?: "安装失败")
                }
            )
        }
    }
}
