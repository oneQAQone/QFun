package me.yxp.qfun.utils.ui

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import me.yxp.qfun.utils.qq.QQCurrentEnv

object ThemeHelper {
    private const val KEY_NIGHT_MODE = "night_mode"

    fun isNightMode(): Boolean {
        return QQCurrentEnv.globalPreference.getBoolean(KEY_NIGHT_MODE, false)
    }

    fun setNightMode(isNight: Boolean) {
        QQCurrentEnv.globalPreference.edit { putBoolean(KEY_NIGHT_MODE, isNight) }
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