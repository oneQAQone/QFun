/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2025 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is an opensource software: you can redistribute it
 * and/or modify it under the terms of the General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the General Public License for more details.
 *
 * You should have received a copy of the General Public License
 * along with this software.
 * If not, see
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
/*
 * Modified for QFun on 2026-11-02: configuration, package and hook integration.
 */
package me.yxp.qfun.hook.purify

import android.content.Context
import android.view.View
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.findMethodOrNull
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.WeakHashMap

@HookItemAnnotation(
    "隐藏QQ空间广告",
    "隐藏QQ空间和好友动态里的广告",
    HookCategory.PURIFY
)
object HideQZoneAD : BaseSwitchHookItem() {

    private var adViewConstructors: List<Constructor<*>> = emptyList()
    private var adBindMethods: List<Method> = emptyList()
    private var recommendAdMethod: Method? = null
    private val hiddenViewStates = WeakHashMap<View, QZoneAdViewState>()

    override fun onInit(): Boolean {
        if (HostInfo.isQQ) {
            adViewConstructors = listOf(
                "com.qzone.reborn.feedpro.widget.comment.QZoneFeedProDetailBottomAdBlockView",
                // Include ad-only base classes to cover Delta, Tianshu and other variants.
                "com.qzone.reborn.feedpro.itemview.ad.QZoneAdBaseMediaFeedProItemView",
                "com.qzone.reborn.feedx.itemview.ad.QZoneAdBaseFeedItemView",
                "com.qzone.reborn.feedx.itemview.ad.tianshu.QZoneAdTianshuBaseFeedItemView",
                "com.qzone.reborn.feedx.itemview.ad.tianshu.QZoneAdTianshuPicWithButtonFeedItemView",
                "com.qzone.reborn.feedpro.itemview.ad.card.QZoneCardAdFeedProItemView",
                "com.qzone.reborn.feedpro.itemview.ad.card.QZoneCardMultiPicAdFeedProItemView",
                "com.qzone.reborn.feedpro.itemview.ad.carousel.QZoneCarouselCardVideoAdFeedProItemView",
                "com.qzone.reborn.feedpro.itemview.ad.contract.QZoneContractCardAdFeedProItemView",
                "com.qzone.reborn.feedpro.itemview.ad.contract.QZoneContractFullFrameAdFeedProItemView",
                "com.qzone.reborn.feedx.itemview.ad.QZoneAdVideoFeedItemView",
                "com.qzone.reborn.feedx.itemview.ad.QZoneAdRewardFeedItemView",
                "com.qzone.reborn.feedx.itemview.ad.QZoneAdPictureFeedItemView",
                "com.qzone.reborn.feedx.itemview.ad.QZoneAdMDPAFeedItemView"
            ).mapNotNull { name ->
                ClassUtils.loadClassOrNull(name)?.declaredConstructors?.firstOrNull {
                    it.parameterTypes.firstOrNull() == Context::class.java
                }
            }
            adBindMethods = findQZoneAdBindMethods(adViewConstructors.map { it.declaringClass })
            return adViewConstructors.isNotEmpty()
        }
        if (HostInfo.isTIM) {
            recommendAdMethod = ClassUtils.loadClassOrNull(
                "com.qzone.proxy.feedcomponent.model.gdt.QZoneAdFeedDataExtKt"
            )?.findMethodOrNull {
                name = "isShowingRecommendAd"
                returnType = boolean
            }
            return recommendAdMethod != null
        }
        return false
    }

    override fun onHook() {
        adViewConstructors.forEach { constructor ->
            constructor.hookAfter(this) { param ->
                hideAdView(param.thisObject as View)
            }
        }
        val adClasses = adViewConstructors.map { it.declaringClass }
        adBindMethods.forEach { method ->
            // Restore our changes before QQ binds again, including when switched off.
            method.hookBefore { param ->
                if (adClasses.any { it.isInstance(param.thisObject) }) {
                    restoreAdView(param.thisObject as View)
                }
            }
            method.hookAfter(this) { param ->
                // Shared binding methods also serve normal feed items; only hide ads.
                if (adClasses.any { it.isInstance(param.thisObject) }) {
                    hideAdView(param.thisObject as View)
                }
            }
        }
        recommendAdMethod?.hookBefore(this) { it.result = true }
    }

    private fun hideAdView(view: View) {
        val layoutParams = view.layoutParams
        synchronized(hiddenViewStates) {
            hiddenViewStates.getOrPut(view) {
                QZoneAdViewState(view.visibility)
            }.rememberDimensions(layoutParams?.width, layoutParams?.height)
        }
        view.visibility = View.GONE
        layoutParams?.let { params ->
            params.height = 0
            params.width = 0
            view.layoutParams = params
        }
    }

    private fun restoreAdView(view: View) {
        val state = synchronized(hiddenViewStates) { hiddenViewStates.remove(view) } ?: return
        view.visibility = state.visibility
        view.layoutParams?.let { params ->
            state.width?.let { params.width = it }
            state.height?.let { params.height = it }
            view.layoutParams = params
        }
    }
}

internal class QZoneAdViewState(val visibility: Int) {
    var width: Int? = null
        private set
    var height: Int? = null
        private set

    fun rememberDimensions(width: Int?, height: Int?) {
        if (this.width == null) this.width = width
        if (this.height == null) this.height = height
    }
}

// QQ 9.3.75 restores visibility while binding. Cover inherited and bridge methods
// so a recycled ad is hidden again after its data has been bound.
internal fun findQZoneAdBindMethods(adClasses: List<Class<*>>): List<Method> =
    adClasses.asSequence()
        .flatMap { clazz -> generateSequence(clazz) { it.superclass } }
        .distinct()
        .flatMap { it.declaredMethods.asSequence() }
        .filter {
            it.name == "bindData" && it.returnType == Void.TYPE &&
                !Modifier.isStatic(it.modifiers) && !Modifier.isAbstract(it.modifiers) &&
                it.parameterTypes.contentEquals(arrayOf(Any::class.java, Int::class.javaPrimitiveType))
        }.toList()
