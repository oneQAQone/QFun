package me.yxp.qfun.hook.entry

import android.content.Context
import android.content.Intent
import me.yxp.qfun.R
import me.yxp.qfun.activity.PluginActivity
import me.yxp.qfun.activity.SettingActivity
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getStaticObject
import me.yxp.qfun.utils.reflect.newInstanceWithArgs
import me.yxp.qfun.utils.reflect.toClass
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Proxy

@HookItemAnnotation("QQ设置入口")
object QQSettingInject : BaseApiHookItem<Listener>(), DexKitTask {

    private const val TOP_TITLE = "模块"
    private const val BOTTOM_TITLE = ""
    private const val MODULE_ORDER = 10

    @Suppress("UNCHECKED_CAST")
    override fun loadHook() {

        val providerClass =
            if (HostInfo.isQQ && HostInfo.versionCode >= 12288) requireClass("provider")
            else "com.tencent.mobileqq.setting.main.NewSettingConfigProvider".clazz
                ?: "com.tencent.mobileqq.setting.main.MainSettingConfigProvider".clazz

        if (providerClass == null) throw ClassNotFoundException("SettingConfigProvider")

        val itemClass = requireClass("item")

        val setOnClickListener = itemClass.findMethod {
            returnType = void
            paramTypes("kotlin.jvm.functions.Function0".toClass)
        }

        providerClass.findMethod {
            returnType = list
            paramTypes(context)
        }.hookAfter(this) { param ->
            val context = param.args[0] as Context
            val result = param.result as MutableList<Any>

            val settingEntry = makeItem(
                itemClass,
                context,
                MODULE_ORDER,
                "QFun",
                R.drawable.ic_launcher
            )
            setOnClickListener.invoke(
                settingEntry, makeProxy(
                    context,
                    SettingActivity::class.java
                )
            )

            val pluginEntry = makeItem(
                itemClass,
                context,
                MODULE_ORDER,
                "JavaPlugin",
                R.drawable.ic_plugin
            )
            setOnClickListener.invoke(
                pluginEntry, makeProxy(
                    context,
                    PluginActivity::class.java
                )
            )

            result.add(
                1,
                result[0].javaClass.newInstanceWithArgs(
                    listOf(settingEntry, pluginEntry),
                    TOP_TITLE,
                    BOTTOM_TITLE,
                    0,
                    null
                )
            )

        }

    }

    private fun makeItem(itemClass: Class<*>, vararg args: Any?): Any {
        return runCatching {
            itemClass.newInstanceWithArgs(*args, null)
        }.getOrElse {
            itemClass.newInstanceWithArgs(*args)
        }
    }

    private fun makeProxy(
        context: Context,
        activityClass: Class<*>
    ): Any {

        val unit = "kotlin.Unit".toClass.getStaticObject("INSTANCE")

        return Proxy.newProxyInstance(
            ClassUtils.hostClassLoader,
            arrayOf("kotlin.jvm.functions.Function0".toClass)
        ) { _, method, _ ->
            if (method.name == "invoke") {
                runCatching {
                    context.startActivity(Intent(context, activityClass))
                }.onFailure {
                    LogUtils.e(this, it)
                }
            }
            unit
        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "provider" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.setting.main")
            matcher {
                superClass("com.tencent.mobileqq.setting.processor.SettingConfigProvider")
            }
        },
        "item" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.setting.processor")
            matcher {
                usingStrings("context", "leftText")
            }
        }
    )

}