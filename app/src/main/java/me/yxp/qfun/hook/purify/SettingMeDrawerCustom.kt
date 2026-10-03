package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.DrawerConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.SettingMeDrawerPage
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethods
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "侧边栏精简",
    "隐藏主页侧边栏中的 相册 / 收藏 / 文件 / 钱包 / 会员中心 / 个性装扮 / 免流量 入口",
    HookCategory.PURIFY
)
object SettingMeDrawerCustom : BaseClickableHookItem<DrawerConfig>(DrawerConfig.serializer()), DexKitTask {

    override val isNeedRestart: Boolean = true

    override val defaultConfig = DrawerConfig()

    private const val BIZ_BEAN = "com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean"

    private const val QUERY_HIDE_PREDICATE = "settingMeHidePredicate"

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        QUERY_HIDE_PREDICATE to FindMethod().apply {
            matcher {
                returnType = "boolean"
                paramTypes(BIZ_BEAN)
            }
        }
    )

    private val idPattern = Regex("^d_[a-z0-9_]+$")

    private lateinit var isItemHidden: Method
    private var idGetters: List<Method> = emptyList()
    private var cachedIdGetter: Method? = null

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        val bean = BIZ_BEAN.clazz ?: return false

        isItemHidden = runCatching { requireMethod(QUERY_HIDE_PREDICATE) }.getOrNull() ?: return false

        idGetters = bean.findMethods {
            returnType = string
            paramCount = 0
        }

        return super.onInit()
    }

    override fun onHook() {
        isItemHidden.hookAfter(this) { param ->
            val id = beanId(param.args[0]) ?: return@hookAfter
            if (id in config.hiddenIds) param.result = true
        }
    }

    private fun beanId(bean: Any?): String? {
        if (bean == null) return null

        cachedIdGetter?.let { getter ->
            val value = runCatching { getter.invoke(bean) as? String }.getOrNull()
            if (value != null && idPattern.matches(value)) return value
        }

        idGetters.forEach { getter ->
            val value = runCatching { getter.invoke(bean) as? String }.getOrNull()
            if (value != null && idPattern.matches(value)) {
                cachedIdGetter = getter
                return value
            }
        }

        return null
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        SettingMeDrawerPage(config, ::updateConfig, onDismiss)
    }

}
