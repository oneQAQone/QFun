package me.yxp.qfun.hook.file

import android.content.pm.PackageManager
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qqnt.kernel.nativeinterface.FileElement
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.json.obj
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.walk
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.setObject
import mqq.app.MSFServlet
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File

@HookItemAnnotation(
    "上传apk重命名",
    "上传apk时自动重命名为应用名称_版本号.APK",
    HookCategory.FILE
)
object AutoRemarkApk : BaseSwitchHookItem(), SendMsgListener {

    override fun onHook() {

        MSFServlet::class.java.getDeclaredMethod(
            "onReceive",
            FromServiceMsg::class.java
        ).hookBefore(this) { param ->
            val msg = param.args[0] as FromServiceMsg
            if ("OidbSvcTrpcTcp.0xe37_800" != msg.serviceCmd) return@hookBefore

            val data = ProtoData().apply { fromBytes(msg.wupBuffer) }
            val json = data.toJSON()

            val fileMeta = json.walk("4", "10")?.obj

            fileMeta?.let { meta ->

                meta.walk("40", "1")?.let { item ->
                    when (item) {
                        is JSONObject -> item.put("5", fixSuffix(item["5"].str))
                        is JSONArray -> for (i in 0 until item.length()) {
                            item.optJSONObject(i)?.let { it.put("5", fixSuffix(it["5"].str)) }
                        }
                    }
                }

                meta.walk("30")?.obj?.let { storage ->
                    storage.put("7", fixSuffix(storage["7"].str))
                }
            }

            val newData = ProtoData().apply { fromJSON(json) }
            msg.setObject("wupBuffer", packWithHeader(newData.toBytes()))
        }
    }

    override fun onSend(elements: ArrayList<MsgElement>) {
        elements.mapNotNull { it.fileElement }
            .filter { it.fileName?.endsWith(".apk", true) == true }
            .forEach(::renameApkFile)
    }

    private fun renameApkFile(fileElement: FileElement) {
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
                    if (safeAppName.isNotEmpty()) parsedName = "${safeAppName}_$versionName.APK"
                }
            }

            fileElement.fileName = parsedName ?: originalName?.takeIf { it.isNotEmpty() }
                ?.replace(".apk", ".APK", ignoreCase = true)
                    ?: "未知应用${System.currentTimeMillis()}.APK"

        } catch (e: Exception) {
            LogUtils.e(this, e)
            fileElement.fileName =
                fileElement.fileName?.replace(".apk", ".APK", ignoreCase = true) ?: "error.APK"
        }
    }

    private fun fixSuffix(name: String?): String? {
        if (name == null) return null
        return if (name.uppercase().matches(Regex(".*\\.APK\\.\\d+$"))) {
            name.replaceFirst(Regex("(?i)\\.apk\\.\\d+$"), ".APK")
        } else name
    }

    private fun packWithHeader(data: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        DataOutputStream(bos).apply {
            writeInt(data.size + 4)
            write(data)
        }
        return bos.toByteArray()
    }
}