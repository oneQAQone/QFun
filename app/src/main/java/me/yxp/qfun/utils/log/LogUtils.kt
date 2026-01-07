package me.yxp.qfun.utils.log

import android.os.Build
import android.util.Log
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseHookItem
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {

    private const val TAG = "QFun"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    fun d(msg: String) {
        FileUtils.writeText(File(QQCurrentEnv.currentDir, "log.txt"), "$msg\n", true)
    }

    fun e(tag: String, t: Throwable) {
        val stackTrace = Log.getStackTraceString(t)
        val msg = "[$tag] Error:\n$stackTrace"
        runCatching {
            XposedBridge.log("[$TAG] [$tag] Error occurred:")
            XposedBridge.log(t)
        }
        saveCrashLog(tag, msg)
    }

    fun e(hookItem: BaseHookItem, t: Throwable) {
        e(hookItem.name, t)
    }

    fun getEnvironmentInfo(): String {
        val bridge = ModuleLoader.getHookBridge()
        return """
                === Environment Information ===
                Record Time: ${dateFormat.format(Date())}

                --- Device Information ---
                Brand: ${Build.BRAND}
                Model: ${Build.MODEL}
                Device: ${Build.DEVICE}
                Product: ${Build.PRODUCT}
                Manufacturer: ${Build.MANUFACTURER}
                Android Version: ${Build.VERSION.RELEASE}
                API Level: ${Build.VERSION.SDK_INT}
                Build ID: ${Build.ID}
                Fingerprint: ${Build.FINGERPRINT}
                Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString(", ")}

                --- Xposed Framework Information ---
                Framework Name: ${bridge?.frameworkName ?: "Unknown"}
                Framework Version: ${bridge?.frameworkVersion ?: "Unknown"}
                Framework Version Code: ${bridge?.frameworkVersionCode ?: "Unknown"}
                API Version: ${bridge?.apiLevel ?: "Unknown"}

                --- Host Application Information ---
                Package Name: ${HostInfo.packageName}
                Version Name: ${HostInfo.versionName}
                Version Code: ${HostInfo.versionCode}

                --- Module Information ---
                Module Version Name: ${BuildConfig.VERSION_NAME}
                Module Version Code: ${BuildConfig.VERSION_CODE}

                ======================================
            """.trimIndent()
    }

    fun logEnvironment() {
        ModuleScope.launchIO("EnvLog") {
            val info = getEnvironmentInfo()
            val file = File("${HostInfo.moduleDataPath}global/log", "environment_info.txt")
            FileUtils.writeText(file, info)
        }
    }

    private fun saveCrashLog(tag: String, content: String) {
        val dir = QQCurrentEnv.currentDir
        ModuleScope.launchIO("WriteLog") {
            val time = dateFormat.format(Date())
            val logContent = "\n=== $time [$tag] ===\n$content\n"
            val file = File("${dir}log", "error_log.txt")
            FileUtils.writeText(file, logContent, true)
        }
    }
}
