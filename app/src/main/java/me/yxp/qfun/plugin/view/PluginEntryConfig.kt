package me.yxp.qfun.plugin.view

import androidx.compose.runtime.Composable
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
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
    const val KEY = "plugin_entry_mode"

    @Composable
    fun Content(onDismiss: () -> Unit) {
        BaseDialogSurface(title = "脚本入口方式") {
            SelectionGroup {
                PluginEntryMode.entries.forEach { entry ->
                    SelectionItem(
                        title = entry.title,
                        subtitle = entry.description,
                        isSelected = mode == entry,
                        onClick = { mode = entry; onDismiss() }
                    )
                }
            }
        }
    }

    var mode: PluginEntryMode
        get() = PluginEntryMode.fromPreference(QQCurrentEnv.globalPreference.getString(KEY, null))
        set(value) {
            QQCurrentEnv.globalPreference.edit { putString(KEY, value.preferenceValue) }
            PluginViewLoader.refreshEntry()
            PluginPlusPanelEntry.refreshPanels()
        }
}
