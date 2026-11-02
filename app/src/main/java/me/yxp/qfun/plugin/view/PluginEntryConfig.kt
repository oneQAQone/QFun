package me.yxp.qfun.plugin.view

import androidx.core.content.edit
import me.yxp.qfun.utils.qq.QQCurrentEnv

enum class PluginEntryMode(val preferenceValue: String, val title: String, val description: String) {
    FLOATING("floating", "悬浮窗", "使用可拖动悬浮图标"),
    PLUS_PANEL("plus_panel", "加号菜单", "在聊天加号面板中显示脚本菜单"),
    LONG_PRESS_PLUS("long_press_plus", "长按加号", "长按聊天加号打开，点击仍显示 QQ 菜单");

    companion object {
        fun fromPreference(value: String?): PluginEntryMode =
            entries.firstOrNull { it.preferenceValue == value } ?: FLOATING
    }
}

object PluginEntryConfig {
    var mode: PluginEntryMode
        get() = PluginEntryMode.fromPreference(QQCurrentEnv.globalPreference.getString("plugin_entry_mode", null))
        set(value) {
            QQCurrentEnv.globalPreference.edit { putString("plugin_entry_mode", value.preferenceValue) }
            PluginViewLoader.refreshEntry()
            PluginPlusPanelEntry.refreshPanels()
        }
}
