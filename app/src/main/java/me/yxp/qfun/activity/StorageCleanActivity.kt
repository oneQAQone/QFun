package me.yxp.qfun.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed  // 改为 itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import me.yxp.qfun.activity.BaseComposeActivity
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.ui.components.atoms.ActionButton
import me.yxp.qfun.ui.components.atoms.ActionButtonStyle
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.dialogs.ConfirmDialog
import me.yxp.qfun.ui.components.molecules.AnimatedListItem  // 新增导入
import me.yxp.qfun.ui.components.molecules.QFunTopBar
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import java.io.File
import kotlin.math.ln
import kotlin.math.pow

class StorageCleanActivity : BaseComposeActivity() {

    private val cleanPaths = linkedMapOf(
        "默认外部cache目录" to "%extra%/cache",
        "默认内部cache目录" to "%private%/cache",
        "照片编辑器" to "%extra%/files/ae",
        "tbs_log" to "%extra%/files/commonlog",
        "tbs_cache" to "%extra%/files/tbs",
        "频道图片缓存" to "%extra%/files/guild",
        "一起听歌缓存" to "%extra%/files/ListenTogether_v828",
        "Process_Log" to "%extra%/files/onelog",
        "appsdk_cache" to "%extra%/files/opensdk_tmp",
        "看点缓存" to "%extra%/files/qcircle",
        "看点缓存2" to "%extra%/qcircle",
        "超级QQ秀缓存" to "%extra%/files/QQShowDownload",
        "QQ钱包缓存" to "%extra%/files/QWallet",
        "app_logs" to "%extra%/files/tencent/",
        "地图缓存文件" to "%extra%/files/tencentmapsdk",
        "视频编辑器缓存" to "%extra%/files/video",
        "超级QQ秀缓存2" to "%extra%/files/zootopia_download",
        "QQ空间缓存" to "%extra%/qzone",
        "msf_report_log" to "%extra%/Tencent/audio",
        "小程序缓存,日志" to "%extra%/Tencent/mini",
        "收藏表情缓存" to "%extra%/Tencent/QQ_Favorite",
        "图片编辑器缓存" to "%extra%/Tencent/QQ_Images",
        "开屏广告缓存(?)" to "%extra%/Tencent/QQ_Shortvideos",
        "miniAppSdk_Lib_cache" to "%extra%/Tencent/wxminiapp",
        "QQ秀" to "%extra%/Tencent/MobileQQ/.apollo",
        "铭牌标志缓存" to "%extra%/Tencent/MobileQQ/.card",
        "炫彩群名片缓存" to "%extra%/Tencent/MobileQQ/.CorlorNick",
        "原创表情缓存" to "%extra%/Tencent/MobileQQ/.emotionsm",
        "特效字体缓存" to "%extra%/Tencent/MobileQQ/.font_effect",
        "字体缓存" to "%extra%/Tencent/MobileQQ/.font_info",
        "头像框缓存" to "%extra%/Tencent/MobileQQ/.pendant",
        "资料卡缓存" to "%extra%/Tencent/MobileQQ/.profilecard",
        "入群特效缓存" to "%extra%/Tencent/MobileQQ/.troop",
        "戳一戳缓存" to "%extra%/Tencent/MobileQQ/.vaspoke",
        "语音缓存" to "%extra%/Tencent/MobileQQ/%uin%/ptt",
        "消息截图缓存" to "%extra%/Tencent/MobileQQ/aio_long_shot",
        "聊天图片缓存" to "%extra%/Tencent/MobileQQ/chatpic",
        "图片缓存" to "%extra%/Tencent/MobileQQ/diskcache",
        "头像缓存" to "%extra%/Tencent/MobileQQ/head",
        "热图缓存" to "%extra%/Tencent/MobileQQ/hotpic",
        "flutter_lib_and_res" to "%extra%/Tencent/MobileQQ/pddata",
        "相片预发送缓存" to "%extra%/Tencent/MobileQQ/photo",
        "涂鸦缓存" to "%extra%/Tencent/MobileQQ/Scribble",
        "短视频缓存" to "%extra%/Tencent/MobileQQ/shortvideo",
        "缩略图(?)" to "%extra%/Tencent/MobileQQ/thumb",
        "群聊段位标志缓存" to "%extra%/Tencent/MobileQQ/troopgamecard",
        "不知道哪里的图片缓存" to "%extra%/Tencent/MobileQQ/troopphoto",
        "不知道哪里的lottie缓存" to "%extra%/Tencent/MobileQQ/vas",
        "作图缓存" to "%extra%/Tencent/MobileQQ/zhitu",
        "WebView_Cache" to "%private%/app_x5webview/Cache",
        "Json卡片缓存" to "%private%/files/ArkApp/Cache",
        "消息气泡缓存" to "%private%/files/bubble_info",
        "未知内部缓存" to "%private%/files/files",
        "已下载的小程序" to "%private%/files/mini",
        "pddata" to "%private%/files/pddata",
        "礼物,花里胡哨的VIP图标缓存" to "%private%/files/vas_material_folder"
    )

    private fun getRealPath(template: String): String {
        val extra = HostInfo.hostContext.getExternalFilesDir(null)?.parentFile?.absolutePath
            ?: "/storage/emulated/0/Android/data/${HostInfo.packageName}"
        val private = HostInfo.hostContext.filesDir.parent ?: HostInfo.hostContext.filesDir.absolutePath
        val uin = QQCurrentEnv.currentUin
        return template
            .replace("%extra%", extra)
            .replace("%private%", private)
            .replace("%uin%", uin)
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt()
        val size = bytes / 1024.0.pow(digitGroups)
        return String.format("%.2f %s", size, units[digitGroups])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QFunTheme(isDarkTheme) {
                StorageCleanScreen()
            }
        }
    }

    @Composable
    private fun StorageCleanScreen() {
        val colors = QFunTheme.colors
        val sizeMap = remember { mutableStateMapOf<String, Long?>() }
        var cleaningItem by remember { mutableStateOf<String?>(null) }
        var confirmItem by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            val semaphore = Semaphore(4)
            cleanPaths.keys.forEach { name ->
                ModuleScope.launch {
                    semaphore.withPermit {
                        val path = getRealPath(cleanPaths[name]!!)
                        val size = withContext(Dispatchers.IO) {
                            try {
                                FileUtils.getDirSize(File(path))
                            } catch (e: Exception) {
                                0L
                            }
                        }
                        sizeMap[name] = size
                    }
                }
            }
        }

        suspend fun cleanItem(name: String, path: String) {
            cleaningItem = name
            withContext(Dispatchers.IO) {
                try {
                    FileUtils.delete(File(path))
                    sizeMap[name] = 0L
                    Toasts.qqToast(4, "已清理: $name")
                } catch (e: Exception) {
                    Toasts.qqToast(1, "清理失败: ${e.message}")
                } finally {
                    cleaningItem = null
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                QFunTopBar(
                    title = "存储空间清理☕",
                    showBackButton = true,
                    onBackClick = { finish() },
                    themeMode = themeMode,
                    onThemeToggle = ::toggleTheme
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(cleanPaths.keys.toList()) { index, name ->
                        val size = sizeMap[name]
                        val path = getRealPath(cleanPaths[name]!!)
                        AnimatedListItem(index) {
                            QFunCard(
                                modifier = Modifier.fillMaxWidth(),
                                animateContentSize = true
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            fontSize = 15.sp,
                                            color = colors.textPrimary
                                        )
                                        if (size == null) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    strokeWidth = 2.dp,
                                                    color = colors.textSecondary
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "计算中...",
                                                    fontSize = 12.sp,
                                                    color = colors.textSecondary
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = formatSize(size),
                                                fontSize = 12.sp,
                                                color = colors.textSecondary
                                            )
                                        }
                                    }
                                    ActionButton(
                                        text = if (cleaningItem == name) "清理中..." else "清理",
                                        onClick = {
                                            if (cleaningItem == null) {
                                                confirmItem = name
                                            }
                                        },
                                        style = ActionButtonStyle.Success
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (confirmItem != null) {
            val name = confirmItem!!
            val path = getRealPath(cleanPaths[name]!!)
            ConfirmDialog(
                visible = true,
                title = "确认清理",
                message = "确定要清理「$name」吗？清理后相关数据将丢失。",
                confirmText = "清理",
                dismissText = "取消",
                onDismiss = { confirmItem = null },
                onConfirm = {
                    confirmItem = null
                    ModuleScope.launch {
                        cleanItem(name, path)
                    }
                }
            )
        }
    }
}
