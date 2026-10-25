package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.MenuConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.MessageMenuPage
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.reflect.findFieldOrNull
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import java.lang.reflect.Method

@HookItemAnnotation(
    "长按菜单精简",
    "按名称隐藏长按消息菜单中的按钮，多个名称用英文逗号隔开，本模块添加的功能不会被隐藏",
    HookCategory.PURIFY
)
object MessageMenuCustom : BaseClickableHookItem<MenuConfig>(MenuConfig.serializer()) {

    override val defaultConfig = MenuConfig()

    private const val MODULE_PREFIX = "[QFun]"

    private lateinit var setMenu: Method

    override fun onInit(): Boolean {
        setMenu = QQCustomMenuExpandableLayout::class.java.findMethod {
            name = "setMenu"
        }

        return super.onInit()
    }

    override fun onHook() {
        setMenu.hookBefore { param ->
            val keywords = config.hiddenNames
                .split(',')
                .map(String::trim)
                .filter(String::isNotEmpty)

            if (keywords.isEmpty()) return@hookBefore

            val items = param.args[0]?.let(::hostItems) ?: return@hookBefore

            items.removeAll { item ->
                !isModuleItem(item) && keywords.any { keyword -> textsOf(item).any { it.contains(keyword) } }
            }
        }
    }

    private fun isModuleItem(item: Any): Boolean = runCatching {
        item.getObjectByTypeOrNull<String>()?.startsWith(MODULE_PREFIX) == true
    }.getOrDefault(false)

    @Suppress("UNCHECKED_CAST")
    private fun hostItems(menu: Any): MutableList<Any>? =
        generateSequence(menu.javaClass) { it.superclass }
            .firstNotNullOfOrNull { clazz ->
                clazz.findFieldOrNull { type = List::class.java }
            }
            ?.get(menu) as? MutableList<Any>

    private fun textsOf(item: Any): List<String> {
        val texts = linkedSetOf<String>()

        item.javaClass.methods
            .filter { it.parameterCount == 0 && it.returnType == String::class.java }
            .forEach { method ->
                runCatching { method.invoke(item) as? String }.getOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?.let(texts::add)
            }

        generateSequence(item.javaClass) { it.superclass }
            .takeWhile { it != Any::class.java }
            .flatMap { it.declaredFields.asSequence() }
            .filter { it.type == String::class.java }
            .forEach { field ->
                runCatching { field.apply { isAccessible = true }.get(item) as? String }.getOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?.let(texts::add)
            }

        return texts.toList()
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        MessageMenuPage(config, ::updateConfig, onDismiss)
    }

}