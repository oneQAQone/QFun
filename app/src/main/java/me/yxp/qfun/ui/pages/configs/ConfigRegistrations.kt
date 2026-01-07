package me.yxp.qfun.ui.pages.configs

import me.yxp.qfun.generated.HookRegistry
import me.yxp.qfun.hook.base.BaseClickableHookItem

object ConfigRegistrations {

    private var initialized = false

    fun initAll() {
        if (initialized) return
        initialized = true

        HookRegistry.hookItems
            .filterIsInstance<BaseClickableHookItem<*>>()
            .forEach { item ->
                ConfigUiRegistry.register(item.name) { onDismiss ->
                    item.ConfigContent(onDismiss)
                }
            }
    }
}
