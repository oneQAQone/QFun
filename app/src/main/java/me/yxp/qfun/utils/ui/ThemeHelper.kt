package me.yxp.qfun.utils.ui

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import com.tencent.mobileqq.vas.theme.api.ThemeUtil
import me.yxp.qfun.utils.qq.QQCurrentEnv

object ThemeHelper {
    private const val KEY_THEME_MODE = "theme_mode" 

    const val MODE_AUTO = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    fun getThemeMode(): Int {
        return QQCurrentEnv.globalPreference.getInt(KEY_THEME_MODE, MODE_AUTO)
    }

    fun setThemeMode(mode: Int) {
        QQCurrentEnv.globalPreference.edit { putInt(KEY_THEME_MODE, mode) }
    }

    fun isNightMode(): Boolean {
        return when (getThemeMode()) {
            MODE_LIGHT -> false
            MODE_DARK -> true
            else -> ThemeUtil.isInNightMode(QQCurrentEnv.qQAppInterface)
        }
    }

    @Suppress("DEPRECATION")
    fun applyTheme(context: Context) {
        val isNight = isNightMode()
        val res = context.resources
        val config = Configuration(res.configuration)
        val mode = if (isNight) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
        config.uiMode = mode or (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
        res.updateConfiguration(config, null)
    }
}