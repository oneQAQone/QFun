package me.yxp.qfun.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import me.yxp.qfun.utils.ui.ThemeHelper

@Suppress("DEPRECATION")
abstract class BaseComposeActivity : ComponentActivity() {

    protected var themeMode by mutableIntStateOf(ThemeHelper.MODE_AUTO)
    protected var isDarkTheme by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setupTransparentStatusBar()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        
        themeMode = ThemeHelper.getThemeMode()
        isDarkTheme = ThemeHelper.isNightMode()
        updateStatusBarAppearance()
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdateTheme()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT < 30) {
            (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (hasFocus) {
            checkAndUpdateTheme()
        }
    }

    private fun checkAndUpdateTheme() {
        if (themeMode == ThemeHelper.MODE_AUTO) {
            val newNightMode = ThemeHelper.isNightMode()
            if (isDarkTheme != newNightMode) {
                isDarkTheme = newNightMode
                updateStatusBarAppearance()
            }
        }
    }

    private fun setupTransparentStatusBar() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = false
            }
        }
    }

    protected fun toggleTheme() {
        // 如果当前是自动模式，点击后根据当前状态切换为强制深色或浅色
        // 如果当前是强制模式，点击后恢复为自动模式
        themeMode = when (themeMode) {
            ThemeHelper.MODE_AUTO -> if (isDarkTheme) ThemeHelper.MODE_LIGHT else ThemeHelper.MODE_DARK
            else -> ThemeHelper.MODE_AUTO
        }
        ThemeHelper.setThemeMode(themeMode)
        isDarkTheme = ThemeHelper.isNightMode()
        ThemeHelper.applyTheme(this)
        updateStatusBarAppearance()
    }

    private fun updateStatusBarAppearance() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !isDarkTheme
        controller.isAppearanceLightNavigationBars = !isDarkTheme
    }
}