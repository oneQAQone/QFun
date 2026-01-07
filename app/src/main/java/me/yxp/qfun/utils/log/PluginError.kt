package me.yxp.qfun.utils.log

import android.util.Log
import me.yxp.qfun.plugin.bean.PluginInfo
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.Toasts
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PluginError {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun evalError(t: Throwable, info: PluginInfo) {
        logPluginError("evalError", t, info, null)
    }

    fun callError(t: Throwable, info: PluginInfo) {
        logPluginError("callError", t, info, null)
    }

    fun findError(t: Throwable, info: PluginInfo, callback: String) {
        logPluginError("findError", t, info, "回调($callback)未找到")
    }

    fun log(info: PluginInfo, fileName: String, text: String) {
        val file = File(info.dirPath, fileName)
        FileUtils.writeText(file, text, true)
    }

    private fun logPluginError(
        errorType: String,
        t: Throwable,
        info: PluginInfo,
        additionalInfo: String?
    ) {
        val time = dateFormat.format(Date())
        val stackTrace = Log.getStackTraceString(t)

        val logContent = buildString {
            append("\n\n\n$errorType\n\n")
            append("Time:$time\n\n")
            if (additionalInfo != null) {
                append("$additionalInfo\n\n")
            }
            append(stackTrace)
        }

        Toasts.toast(t.toString())

        log(info, "error.txt", logContent)
    }
}