package me.yxp.qfun.hook

import androidx.core.content.edit
import com.tencent.mobileqq.app.QQAppInterface
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.generated.HookRegistry
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.plugin.MainPlugin
import me.yxp.qfun.ui.pages.configs.ConfigUiRegistry
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.net.HttpUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.TAG
import me.yxp.qfun.utils.reflect.findMethod

object MainHook {

    private var initialized = false
    private val allHookItem = HookRegistry.hookItems
    private val apiHookItemList =
        allHookItem.filterIsInstance<BaseApiHookItem<Listener>>()
    val switchHookItemList =
        allHookItem.filterIsInstance<BaseSwitchHookItem>()
    val clickableHookItemList =
        switchHookItemList.filterIsInstance<BaseClickableHookItem<*>>()


    fun loadHook() {

        loadApiHook()
        initSwitchHookItem()
        hookAccountChange()


    }

    private fun loadApiHook() = apiHookItemList
        .forEach {
            try {
                if (it.isInTargetProcess()) it.loadHook()
            } catch (t: Throwable) {
                LogUtils.e(it, t)
            }
        }

    fun initAllConfigUI() {
        if (initialized) return
        initialized = true

        clickableHookItemList
            .forEach { item ->
                ConfigUiRegistry.register(item.name) { onDismiss ->
                    item.ConfigContent(onDismiss)
                }
            }
    }

    private fun initSwitchHookItem() = switchHookItemList.forEach(BaseSwitchHookItem::init)

    fun processDataForCurrent(tag: String) = clickableHookItemList
        .filter(BaseSwitchHookItem::isAvailable)
        .forEach {
            when (tag) {
                "init" -> it.initData()
                "save" -> it.saveData()
            }
        }


    private fun hookAccountChange() {
        QQAppInterface::class.java.findMethod {
            name = "onCreate"
        }.hookAfter {
            QQCurrentEnv.globalPreference.edit {
                putString(
                    "currentUin",
                    QQCurrentEnv.currentUin
                )
            }
            processDataForCurrent("init")
            MainPlugin.initAllPluginForCurrent()

        }
    }

}