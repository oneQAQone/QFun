package me.yxp.qfun.plugin.view

import android.view.View
import me.yxp.qfun.R
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.dexkit.DexKitCache
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.reflect.TAG
import org.luckypray.dexkit.DexKitBridge
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.callMethod
import java.lang.reflect.Field
import java.util.WeakHashMap
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher

@HookItemAnnotation("脚本加号面板入口")
object PluginPlusPanelEntry : BaseApiHookItem<Listener>(), DexKitTask {
    private lateinit var panelItem: Any
    private lateinit var pagerItems: Field
    private val pagers = WeakHashMap<Any, Boolean>()
    private var refreshing = false
    private var ready = false

    override fun getQueryMap(): Map<String, BaseMatcher> = if (!HostInfo.isQQ) emptyMap() else mapOf(
        "data" to FindClass().apply {
            searchPackages("com.tencent.qqnt.pluspanel.data")
            matcher { usingStrings("PluginData{drawableData=") }
        },
        "pager" to FindClass().apply {
            searchPackages("com.tencent.qqnt.pluspanel.adapter")
            matcher { usingStrings("updateRecyclerView PlusPanelAppListAdapter page = ") }
        },
        "adapter" to FindClass().apply {
            searchPackages("com.tencent.qqnt.pluspanel.adapter")
            matcher { usingStrings("app_name", "red_point_type", "elementId") }
        }
    )

    private fun resolveMissingQueries() {
        val missing = getQueryMap().filterKeys { runCatching { requireClass(it) }.isFailure }
        if (missing.isEmpty()) return
        System.loadLibrary("dexkit")
        DexKitBridge.create(HostInfo.hostContext.applicationInfo.sourceDir).use { bridge ->
            missing.forEach { (key, query) ->
                val descriptor = bridge.findClass(query as FindClass).single().descriptor
                DexKitCache.cacheMap["${TAG}->$key"] = descriptor
            }
        }
        DexKitCache.saveCache()
    }

    override fun loadHook() {
        if (!HostInfo.isQQ) return
        resolveMissingQueries()
        val data = requireClass("data")
        val pager = requireClass("pager")
        val iconField = data.getDeclaredField("j").apply { isAccessible = true }
        val iconConstructor = iconField.type.getDeclaredConstructor(
            String::class.java, String::class.java, String::class.java, Int::class.javaPrimitiveType
        )
        panelItem = data.getDeclaredConstructor().newInstance().also { item ->
            data.getDeclaredField("a").apply { isAccessible = true }.setInt(item, -44001)
            data.getDeclaredField("b").apply { isAccessible = true }.setInt(item, -44001)
            for (field in listOf("d", "e")) {
                data.getDeclaredField(field).apply { isAccessible = true }.set(item, "脚本菜单")
            }
            iconField.set(item, iconConstructor.newInstance(null, null, null, R.drawable.ic_script_menu))
        }
        pagerItems = pager.declaredFields.single { it.type == ArrayList::class.java }.apply { isAccessible = true }
        pager.declaredMethods.single {
            it.returnType == Void.TYPE && it.parameterTypes.contentEquals(arrayOf(ArrayList::class.java))
        }.hookBefore(this) { param ->
            pagers[param.thisObject] = true
            @Suppress("UNCHECKED_CAST")
            val items = param.args[0] as List<Any>
            param.args[0] = panelItems(items)
        }
        pager.getDeclaredMethod("getItemPosition", Any::class.java).hookBefore(this) {
            if (refreshing) it.result = -2 // PagerAdapter.POSITION_NONE: rebuild pages after changing modes.
        }
        val isSimpleUi = ClassUtils.loadClass("com.tencent.mobileqq.utils.QQTheme")
            .getDeclaredMethod("isNowSimpleUI")
        var iconResource = R.drawable.ic_script_menu
        val bindItem = requireClass("adapter").declaredMethods.single {
            it.name == "onBindViewHolder" && it.parameterCount == 2
        }
        val itemViewField = bindItem.parameterTypes[0].getField("itemView")
        bindItem.hookBefore(this) {
            // Let QQ apply its own tile layout and theme treatment to the selected artwork.
            val resource = if (isSimpleUi.invoke(null) == true) {
                R.drawable.ic_script_menu
            } else {
                R.drawable.ic_script_menu_filled
            }
            if (resource != iconResource) {
                iconField.set(panelItem, iconConstructor.newInstance(null, null, null, resource))
                iconResource = resource
            }
        }
        bindItem.hookAfter(this) { param ->
            val holder = param.args[0] ?: return@hookAfter
            val view = itemViewField.get(holder) as View
            if (view.tag === panelItem) {
                view.setOnClickListener {
                    if (PluginEntryConfig.mode == PluginEntryMode.PLUS_PANEL) {
                        QQCurrentEnv.activity?.let { activity ->
                            ModuleScope.launchMain { PluginView(activity).showMenu() }
                        }
                    }
                }
                view.setOnLongClickListener { true }
            }
        }
        ready = true
    }

    private fun panelItems(items: List<Any>): ArrayList<Any> = ArrayList<Any>().apply {
        if (ready && PluginEntryConfig.mode == PluginEntryMode.PLUS_PANEL) add(panelItem)
        addAll(items.filterNot { it === panelItem })
    }

    fun refreshPanels() {
        if (!ready) return
        for (pager in pagers.keys.toList()) {
            runCatching {
                @Suppress("UNCHECKED_CAST")
                val items = pagerItems.get(pager) as? List<Any> ?: return@runCatching
                pagerItems.set(pager, panelItems(items))
                refreshing = true
                try {
                    pager.callMethod("notifyDataSetChanged")
                } finally {
                    refreshing = false
                }
            }.onFailure { LogUtils.e(this, it) }
        }
    }
}
