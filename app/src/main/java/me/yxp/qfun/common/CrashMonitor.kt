package me.yxp.qfun.common

import android.content.Intent
import android.util.Log
import me.yxp.qfun.activity.CrashActivity
import me.yxp.qfun.hook.other.CrashDialog
import me.yxp.qfun.loader.hookapi.HookEngineManager
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.log.CrashReporter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import kotlin.system.exitProcess

object CrashMonitor {

    private var isInitialized = false

    fun init() {
        if (isInitialized) return
        isInitialized = true

        try {
            Thread::class.java.getDeclaredMethod(
                "setDefaultUncaughtExceptionHandler",
                Thread.UncaughtExceptionHandler::class.java
            ).hookBefore { param ->
                val originalHandler = param.args[0] as? Thread.UncaughtExceptionHandler
                if (originalHandler !is CrashHandler) {
                    param.args[0] = CrashHandler(originalHandler)
                }
            }

            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (currentHandler !is CrashHandler) {
                Thread.setDefaultUncaughtExceptionHandler(CrashHandler(currentHandler))
            }
        } catch (t: Throwable) {
            HookEngineManager.engine.log(Log.ERROR, "[QFun]", "CrashMonitor init failed: ", t)
        }
    }

    class CrashHandler(private val originalHandler: Thread.UncaughtExceptionHandler?) :
        Thread.UncaughtExceptionHandler {

        override fun uncaughtException(t: Thread, e: Throwable) {
            try {
                val result = CrashReporter.generateReport(t, e)

                val isForeground = QQCurrentEnv.activity != null
                val canShowDialog = runCatching {
                    val config = CrashDialog.config
                    CrashDialog.isEnable &&
                            (config.foregroundDialog || !isForeground) &&
                            (!config.backgroundSilent || isForeground) &&
                            (!config.mainProcessOnly || HostInfo.processName == HostInfo.packageName) &&
                            (!config.childThreadSilent || t.name == "main")
                }.getOrDefault(true)
                if (!canShowDialog) {
                    if (t.name == "main") exitProcess(10)
                    return
                }

                val context = HostInfo.hostContext
                val intent = Intent(context, CrashActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra("blamedModule", result.blamedModule)
                    putExtra("exceptionType", result.exceptionType)
                    putExtra("summary", result.summary)
                    putExtra("reportPath", result.zipFile.absolutePath)
                    putExtra("stackTrace", result.stackTrace)
                    putExtra("hostName", HostInfo.hostName)
                }

                context.startActivity(intent)
                exitProcess(10)

            } catch (innerEx: Throwable) {
                innerEx.printStackTrace()
                originalHandler?.uncaughtException(t, e)
            }
        }
    }
}