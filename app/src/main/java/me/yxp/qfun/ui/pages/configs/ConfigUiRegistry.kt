package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable

object ConfigUiRegistry {

    private val registry = mutableMapOf<String, @Composable (onDismiss: () -> Unit) -> Unit>()

    fun register(configKey: String, content: @Composable (onDismiss: () -> Unit) -> Unit) {
        registry[configKey] = content
    }

    fun getConfigUi(configKey: String): (@Composable (onDismiss: () -> Unit) -> Unit)? {
        return registry[configKey]
    }

    fun clear() {
        registry.clear()
    }
}
