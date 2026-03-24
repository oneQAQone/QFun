package me.yxp.qfun.hook.plugin

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem

@HookItemAnnotation(
    "Java脚本悬浮球",
    "关闭后不再显示脚本悬浮球",
    HookCategory.OTHER,
    "All"
)
object PluginFloatWindowSwitch : BaseSwitchHookItem() {
    override var isEnable: Boolean by BooleanPreference(name, true)
}
