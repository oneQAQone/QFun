package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.MenuConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.MessageMenuPage
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import java.lang.reflect.Method

@HookItemAnnotation(
    "长按菜单精简",
    "自定义长按消息菜单显示哪些项，可逐项关闭 复制 / 引用 / 转发 / 添加表情 / 查找相关表情 等",
    HookCategory.PURIFY
)
object MessageMenuCustom : BaseClickableHookItem<MenuConfig>(MenuConfig.serializer()) {

    override val defaultConfig = MenuConfig()

    private const val MENU_ITEM_BASE = "com.tencent.qqnt.aio.menu.ui.e"

    private val keyPattern = Regex("^[A-Za-z][A-Za-z0-9_]*MenuItem$")

    private var keyGetters: List<Method> = emptyList()

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        val base = MENU_ITEM_BASE.clazz ?: return false

        keyGetters = base.findMethods {
            returnType = string
            paramCount = 0
        }

        return super.onInit()
    }

    override fun onHook() {
        QQCustomMenuExpandableLayout::class.java
            .findMethod { name = "setMenu" }
            .hookBefore(this) { param ->
                if (config.hiddenKeys.isEmpty()) return@hookBefore

                val customMenu = param.args[0] ?: return@hookBefore
                val items = customMenu
                    .getObjectByTypeOrNull<MutableList<Any>>(customMenu.javaClass.superclass)
                if (items.isNullOrEmpty()) return@hookBefore

                items.removeAll { isHidden(it) }
            }
    }

    private fun isHidden(item: Any): Boolean {
        val text = runCatching { item.getObjectByTypeOrNull<String>() }.getOrNull()
        if (text != null && text in config.hiddenKeys) return true

        return keyGetters.any { getter ->
            val key = runCatching { getter.invoke(item) as? String }.getOrNull()
            key != null && keyPattern.matches(key) && key in config.hiddenKeys
        }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        MessageMenuPage(config, ::updateConfig, onDismiss)
    }

}
