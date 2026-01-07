package me.yxp.qfun.hook.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.KSerializer
import me.yxp.qfun.utils.io.ObjectStore

abstract class BaseClickableHookItem<C : Any>(
    private val serializer: KSerializer<C>
) : BaseSwitchHookItem() {

    protected abstract val defaultConfig: C

    protected var configState: C by mutableStateOf(defaultConfig)

    val config: C get() = configState

    open fun initData() {
        configState = ObjectStore.load("data", name, serializer) ?: defaultConfig
    }

    open fun saveData() {
        ObjectStore.save("data", name, configState, serializer)
    }

    fun updateConfig(newConfig: C) {
        configState = newConfig
        saveData()
    }

    @Composable
    abstract fun ConfigContent(onDismiss: () -> Unit)
}
