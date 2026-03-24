package me.yxp.qfun.hook.file

import android.content.pm.PackageManager
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import java.io.File

@HookItemAnnotation(
    "上传apk重命名",
    "上传apk时自动重命名为应用名称_版本号.APK",
    HookCategory.FILE
)
object AutoRemarkApk : BaseSwitchHookItem(), SendMsgListener {

    override fun onSend(elements: ArrayList<MsgElement>) {
        elements.mapNotNull { it.fileElement }
            .filter { it.fileName?.endsWith(".apk", ignoreCase = true) == true }
            .forEach { fileElement ->
                try {
                    val filePath = fileElement.filePath
                    val originalName = fileElement.fileName
                    var parsedName: String? = null

                    if (!filePath.isNullOrEmpty() && File(filePath).exists()) {
                        val pm = HostInfo.hostContext.packageManager
                        val packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_META_DATA)
                        
                        packageInfo?.applicationInfo?.let { appInfo ->
                            appInfo.sourceDir = filePath
                            appInfo.publicSourceDir = filePath
                            
                            val appName = appInfo.loadLabel(pm).toString()
                            val versionName = packageInfo.versionName ?: "未知版本"
                            val safeAppName = appName.replace(Regex("[\\\\/:*?\"<>|]"), "").trim()
                            
                            if (safeAppName.isNotEmpty()) {
                                parsedName = "${safeAppName}_$versionName.APK"
                            }
                        }
                    }

                    val finalName = parsedName 
                        ?: originalName?.takeIf { it.isNotEmpty() }?.replace(".apk", ".APK", ignoreCase = true) 
                        ?: "未知应用${System.currentTimeMillis()}.APK"
                    
                    fileElement.fileName = finalName
                } catch (e: Exception) {
                    LogUtils.e(this, e)
                    val fallbackName = fileElement.fileName?.takeIf { it.isNotEmpty() }
                        ?.replace(".apk", ".APK", ignoreCase = true)
                        ?: "未知应用${System.currentTimeMillis()}.APK"
                    
                    fileElement.fileName = fallbackName
                }
            }
    }
}
