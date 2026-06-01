package me.yxp.qfun.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.ui.pages.crash.CrashScreen
import me.yxp.qfun.utils.qq.AppRestartUtils
import me.yxp.qfun.utils.qq.HostInfo

class CrashActivity : BaseComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                restartApp()
            }
        })

        val blamedModule = intent.getStringExtra("blamedModule") ?: "Unknown"
        val exceptionType = intent.getStringExtra("exceptionType") ?: "Error"
        val summary = intent.getStringExtra("summary") ?: ""
        val reportPath = intent.getStringExtra("reportPath") ?: ""
        val stackTrace = intent.getStringExtra("stackTrace") ?: ""
        val hostName = intent.getStringExtra("hostName") ?: HostInfo.hostName

        setContent {
            QFunTheme(isDarkTheme) {
                CrashScreen(
                    hostName = hostName,
                    blamedModule = blamedModule,
                    exceptionType = exceptionType,
                    summary = summary,
                    reportPath = reportPath,
                    stackTrace = stackTrace,
                    onRestart = ::restartApp,
                )
            }
        }
    }

    private fun restartApp() {
        AppRestartUtils.restartApp(this, "恢复中...")
    }

}
