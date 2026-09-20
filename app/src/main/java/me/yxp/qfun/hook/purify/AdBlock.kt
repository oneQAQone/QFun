package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.AdConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.AdBlockPage
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethodOrNull
import me.yxp.qfun.utils.reflect.findMethods

@HookItemAnnotation(
    "广告净化",
    "按位置屏蔽广告：空间信息流 / GDT 原生与横幅 / 开屏加载页 / 动态购物红点，可分别开关",
    HookCategory.PURIFY
)
object AdBlock : BaseClickableHookItem<AdConfig>(AdConfig.serializer()) {

    override val defaultConfig = AdConfig()

    override val isNeedRestart: Boolean = true

    private const val QZONE_FEED_TYPE = "com.qzone.reborn.feedx.itemview.QZoneFeedType"

    private val QZONE_AD_DETECTORS = listOf(
        "com.qzone.reborn.feedx.itemview.ad.a",
        "com.qzone.reborn.feedx.itemview.ad.b",
        "com.qzone.reborn.feedx.itemview.ad.c",
        "com.qzone.reborn.feedx.itemview.ad.d",
        "com.qzone.reborn.feedx.itemview.ad.gesturestage.a",
        "com.qzone.reborn.feedx.itemview.ad.gesturestage.b",
        "com.qzone.reborn.feedx.itemview.ad.shake.a",
        "com.qzone.reborn.feedx.itemview.ad.shake.b",
        "com.qzone.reborn.feedx.itemview.ad.shake.c",
        "com.qzone.reborn.feedx.itemview.ad.shake.d",
        "com.qzone.reborn.feedx.itemview.ad.shake.e",
        "com.qzone.reborn.feedx.itemview.ad.shake.f",
        "com.qzone.reborn.feedx.itemview.ad.tianshu.a",
        "com.qzone.reborn.feedx.itemview.ad.tianshu.b",
        "com.qzone.reborn.feedx.itemview.ad.tianshu.e"
    )

    private var feedTypeClass: Class<*>? = null
    private var hideType: Any? = null

    private fun blocked(key: String) = key in config.blocked

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        feedTypeClass = QZONE_FEED_TYPE.clazz
        hideType = feedTypeClass?.enumConstants
            ?.firstOrNull { (it as? Enum<*>)?.name == "UNIQUE_TYPE_HIDE" }

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
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        AdBlockPage(config, ::updateConfig, onDismiss)
    }

}
