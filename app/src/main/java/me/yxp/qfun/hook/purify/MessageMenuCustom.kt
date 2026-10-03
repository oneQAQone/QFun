package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.MenuConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.MessageMenuPage
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import java.lang.reflect.Method

@HookItemAnnotation(
    "长按菜单精简",
    "自定义长按消息菜单显示哪些项，可逐项关闭 复制 / 引用 / 转发 / 添加表情 / 查找相关表情 等",
    HookCategory.PURIFY
)
object MessageMenuCustom : BaseClickableHookItem<MenuConfig>(MenuConfig.serializer()) {

    override val defaultConfig = MenuConfig()

    private const val MENU_UI_PACKAGE = "com.tencent.qqnt.aio.menu.ui"



    private val keyPattern = Regex("^[A-Za-z][A-Za-z0-9_]*MenuItem$")

    private var keyGetters: List<Method> = emptyList()

    /** 菜单项基类：长按菜单 ui 包内，唯一"带无参 String getter（菜单项 key）"的类 */
    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        val base = resolveMenuItemClass() ?: return false

        keyGetters = base.findMethods {
            returnType = string
            paramCount = 0
        }

        return super.onInit()
    }

    /** 菜单项基类：ui 包内、直接继承 Object、且带多个"无参 String getter"的类（按结构找，不写死类名） */
    private fun resolveMenuItemClass(): Class<*>? = runCatching {
        System.loadLibrary("dexkit")
        val context = HostInfo.hostContext
        DexKitBridge.create(context.applicationInfo.sourceDir).use { bridge ->
            bridge.findClass(FindClass().apply { searchPackages(MENU_UI_PACKAGE) })
                .firstOrNull { classData ->
                    classData.superClass?.name == "java.lang.Object" &&
                        classData.methods.count {
                            it.paramCount == 0 && it.returnTypeName == "java.lang.String"
                        } >= 2
                }
                ?.getInstance(context.classLoader)
        }
    }.onFailure { LogUtils.e(this, it) }.getOrNull()

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
