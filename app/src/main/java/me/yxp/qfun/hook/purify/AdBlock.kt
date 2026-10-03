package me.yxp.qfun.hook.purify

import android.view.View
import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.AdConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.AdBlockPage
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.ItemBindHooks
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethodOrNull
import me.yxp.qfun.utils.reflect.findMethods
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "广告净化",
    "按位置屏蔽广告：空间信息流 / GDT 原生与横幅 / 开屏加载页 / 动态购物红点，可分别开关",
    HookCategory.PURIFY
)
object AdBlock : BaseClickableHookItem<AdConfig>(AdConfig.serializer()), DexKitTask {

    override val defaultConfig = AdConfig()

    override val isNeedRestart: Boolean = true

    private const val QZONE_FEED_TYPE = "com.qzone.reborn.feedx.itemview.QZoneFeedType"

    private val QZONE_AD_DETECTORS: List<String> by lazy {
        resolveAdDetectorClasses("com.qzone.reborn.feedx.itemview.ad", QZONE_FEED_TYPE)
    }

    private var feedTypeClass: Class<*>? = null
    private var hideType: Any? = null

    private const val FEED_PRO_TYPE = "com.qzone.reborn.feedpro.itemview.QzoneFeedProType"

    private val FEED_PRO_AD_DETECTORS: List<String> by lazy {
        resolveAdDetectorClasses("com.qzone.reborn.feedpro.itemview.ad", FEED_PRO_TYPE)
    }

    /** 动态找出广告判定器：包内"无参 + 返回对应 Feed 类型枚举"的方法所属类 */
    private fun resolveAdDetectorClasses(packageName: String, returnTypeName: String): List<String> = runCatching {
        System.loadLibrary("dexkit")
        val context = HostInfo.hostContext
        DexKitBridge.create(context.applicationInfo.sourceDir).use { bridge ->
            bridge.findMethod(
                FindMethod().apply {
                    searchPackages(packageName)
                    matcher {
                        this.returnType = returnTypeName
                        paramCount = 0
                    }
                }
            ).map { it.declaredClassName }.distinct()
        }
    }.getOrDefault(emptyList())

    private var feedProEmptyType: Any? = null

    private const val AD_ITEM_VIEW_PACKAGE = ".itemview.ad."

    private var recommendPreloadAd: Method? = null

    private fun blocked(key: String) = key in config.blocked

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        feedTypeClass = QZONE_FEED_TYPE.clazz
        hideType = feedTypeClass?.enumConstants
            ?.firstOrNull { (it as? Enum<*>)?.name == "UNIQUE_TYPE_HIDE" }

        feedProEmptyType = FEED_PRO_TYPE.clazz?.enumConstants
            ?.firstOrNull { (it as? Enum<*>)?.name == "UNIQUE_TYPE_UNKNOWN" }

        recommendPreloadAd = runCatching { requireMethod("recommendPreloadAd") }.getOrNull()

        return super.onInit()
    }

    override fun onHook() {

        val typeClass = feedTypeClass
        val hidden = hideType
        if (typeClass != null && hidden != null) {
            QZONE_AD_DETECTORS.forEach { name ->
                name.clazz
                    ?.findMethodOrNull {
                        returnType = typeClass
                        paramCount = 0
                    }
                    ?.hookReplace(this) { param ->
                        if (blocked(AdConfig.QZONE_FEED)) hidden else param.invokeOriginal()
                    }
            }
        }

        "com.qzone.reborn.feedx.block.ad".clazz
            ?.findMethodOrNull {
                name = "getItemCount"
                returnType = int
            }
            ?.hookReplace(this) { param ->
                if (blocked(AdConfig.QZONE_FEED)) 0 else param.invokeOriginal()
            }

        "com.tencent.gdtad.aditem.GdtAdLoader".clazz
            ?.findMethodOrNull {
                name = "load"
                returnType = void
            }
            ?.hookReplace(this) { param ->
                if (blocked(AdConfig.GDT_NATIVE)) null else param.invokeOriginal()
            }

        "com.tencent.gdtad.api.banner.impl.GdtBannerAdAPIImpl".clazz
            ?.findMethodOrNull { name = "buildBannerAd" }
            ?.hookReplace(this) { param ->
                if (blocked(AdConfig.GDT_BANNER)) null else param.invokeOriginal()
            }

        "com.tencent.gdtad.aditem.GdtPreLoader".clazz
            ?.findMethods {
                returnType = void
                paramCount = 1
            }
            ?.forEach { method ->
                method.hookReplace(this) { param ->
                    if (blocked(AdConfig.GDT_PRELOAD)) null else param.invokeOriginal()
                }
            }

        "com.tencent.mobileqq.ad.api.impl.LoadingAdApiImpl".clazz?.let { api ->
            listOf("requestAd", "showAd", "preloadAd").forEach { name ->
                api.findMethodOrNull {
                    this.name = name
                    returnType = void
                }?.hookReplace(this) { param ->
                    if (blocked(AdConfig.LOADING_AD)) null else param.invokeOriginal()
                }
            }
        }

        "com.tencent.mobileqq.ad.api.impl.AdApiImpl".clazz.let { api ->
            api?.findMethodOrNull {
                name = "requestLebaShoppingRedTouchAd"
                returnType = void
            }?.hookReplace(this) { param ->
                if (blocked(AdConfig.LEBA_SHOPPING)) null else param.invokeOriginal()
            }
            api?.findMethodOrNull { name = "getLebaShoppingRedTouchAd" }
                ?.hookReplace(this) { param ->
                    if (blocked(AdConfig.LEBA_SHOPPING)) null else param.invokeOriginal()
                }
        }

        val proType = FEED_PRO_TYPE.clazz
        val proEmpty = feedProEmptyType
        if (proType != null && proEmpty != null) {
            FEED_PRO_AD_DETECTORS.forEach { name ->
                name.clazz
                    ?.findMethodOrNull {
                        returnType = proType
                        paramCount = 0
                    }
                    ?.hookReplace(this) { param ->
                        if (blocked(AdConfig.QZONE_FEED)) proEmpty else param.invokeOriginal()
                    }
            }
        }

        recommendPreloadAd?.hookReplace(this) { param ->
            if (blocked(AdConfig.QZONE_RECOMMEND_AD)) null else param.invokeOriginal()
        }

        installAdItemFilter()
    }

    private fun installAdItemFilter() {
        ItemBindHooks.install(this) { _, itemView ->
            if (!blocked(AdConfig.QZONE_FEED)) return@install
            if (!itemView.javaClass.name.contains(AD_ITEM_VIEW_PACKAGE)) return@install
            runCatching {
                itemView.visibility = View.GONE
                itemView.layoutParams?.height = 0
                itemView.requestLayout()
            }.onFailure { LogUtils.e(this, it) }
        }
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "recommendPreloadAd" to FindMethod().apply {
            matcher {
                usingStrings("handleRecommendFeedPreloadAdRequest skip, not dual tab mode")
            }
        }
    )

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        AdBlockPage(config, ::updateConfig, onDismiss)
    }

}
