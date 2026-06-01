package me.yxp.qfun.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.utils.qq.AppRestartUtils

object ModuleUpdateMonitor {

    private var isInitialized = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
                val packageName = intent.data?.schemeSpecificPart ?: return
                if (packageName == BuildConfig.APPLICATION_ID) {
                    AppRestartUtils.restartApp(context, "模块更新中...")
                }
            }
        }
    }

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
    }
}