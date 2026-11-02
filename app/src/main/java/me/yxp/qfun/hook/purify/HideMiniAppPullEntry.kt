/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2023 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the qwq233 Universal License
 * as published on https://github.com/qwq233/license; either
 * version 2 of the License, or any later version and our EULA as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the qwq233 Universal License for more details.
 *
 * See
 * <https://github.com/qwq233/license>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
/*
 * Modified for QFun on 2026-11-02: configuration, package and hook integration.
 */
package me.yxp.qfun.hook.purify

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitCache
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.reflect.TAG
import org.luckypray.dexkit.DexKitBridge
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.callMethod
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.WeakHashMap
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher

@HookItemAnnotation(
    "隐藏下拉小程序",
    "隐藏消息列表下拉的小程序入口",
    HookCategory.PURIFY
)
object HideMiniAppPullEntry : BaseSwitchHookItem(), DexKitTask {

    private lateinit var headerClass: Class<*>
    private lateinit var headerMoving: Method
    private lateinit var headerState: MiniAppHeaderState
    private lateinit var miniAppContent: Field
    private var contentMovingCallbacks: List<Method> = emptyList()
    private var refreshCallback: Method? = null

    private fun resolveMissingQueries() {
        val missing = getQueryMap().filterKeys { runCatching { requireMethod(it) }.isFailure }
        if (missing.isEmpty()) return
        System.loadLibrary("dexkit")
        DexKitBridge.create(HostInfo.hostContext.applicationInfo.sourceDir).use { bridge ->
            missing.forEach { (key, query) ->
                val descriptor = bridge.findMethod(query as FindMethod).single().descriptor
                DexKitCache.cacheMap["${TAG}->$key"] = descriptor
            }
        }
        DexKitCache.saveCache()
    }

    override fun onInit(): Boolean {
        if (!HostInfo.isQQ) return false
        val newHeader = ClassUtils.loadClassOrNull(NEW_HEADER)
        headerClass = newHeader ?: ClassUtils.loadClassOrNull(OLD_HEADER) ?: return false
        val enableTwoLevel = findMiniAppTwoLevelField(headerClass, HostInfo.versionCode, newHeader == null)
        headerState = MiniAppHeaderState(enableTwoLevel)
        miniAppContent = findMiniAppContentField(headerClass)
        contentMovingCallbacks = findMiniAppMovingCallbacks(headerClass)
        check(contentMovingCallbacks.isNotEmpty()) { "Mini app movement callback not found" }
        headerMoving = enableTwoLevel.declaringClass.getDeclaredMethod(
            "onMoving", Boolean::class.javaPrimitiveType, Float::class.javaPrimitiveType,
            Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
        )
        resolveMissingQueries()
        refreshCallback = when {
            newHeader != null && miniAppNeedsLegacyRefreshHook(HostInfo.versionCode) ->
                requireMethod("refreshStateChanged")
            newHeader == null -> headerClass.declaredMethods.single {
                it.name == "a" && it.parameterCount == 3
            }
            else -> null
        }
        return true
    }

    override fun onHook() {
        hookPullDistance()
        // Initialize normally so disabling the switch can restore the same header.
        headerClass.declaredConstructors.forEach { constructor ->
            constructor.hookAfter { headerState.apply(it.thisObject, isEnable) }
        }
        headerMoving.hookBefore {
            if (headerClass.isInstance(it.thisObject)) headerState.apply(it.thisObject, isEnable)
        }
        contentMovingCallbacks.forEach { method ->
            method.hookAfter(this) {
                if (headerClass.isInstance(it.thisObject)) {
                    // QQ shows the panel independently of its two-level entry flag.
                    // Leave the separate ConversationPullRefreshHeaderView untouched.
                    (miniAppContent.get(it.thisObject) as? View)?.visibility = View.GONE
                }
            }
        }
        // QQ 9.3.30+ must complete its own refresh lifecycle for message receiving.
        refreshCallback?.hookAfter(this) { it.args[0]?.callMethod("finishRefresh") }
    }

    private fun hookPullDistance() {
        val layoutClass = ClassUtils.loadClassOrNull(
            "com.qqnt.widget.smartrefreshlayout.layout.SmartRefreshLayout"
        ) ?: return
        // Discover the kernel by its stable API rather than its obfuscated class name.
        val movingParams = arrayOf(Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
        val moveSpinner = layoutClass.declaredClasses.mapNotNull { nested ->
            nested.declaredMethods.singleOrNull {
                it.name == "moveSpinner" && it.parameterTypes.contentEquals(movingParams)
            }
        }.singleOrNull() ?: return
        val layoutField = moveSpinner.declaringClass.declaredFields.single { it.type == layoutClass }
            .apply { isAccessible = true }
        moveSpinner.hookBefore(this) {
            if (it.args[1] != true) return@hookBefore
            val layout = layoutField.get(it.thisObject) as ViewGroup
            // Other screens use this refresh widget too; only limit the mini app header.
            if ((0 until layout.childCount).none { index -> headerClass.isInstance(layout.getChildAt(index)) }) {
                return@hookBefore
            }
            it.args[0] = limitMiniAppPullOffset(it.args[0] as Int, layout.resources.displayMetrics.density)
        }
    }

    override fun getQueryMap(): Map<String, BaseMatcher> {
        if (!HostInfo.isQQ) return emptyMap()
        return buildMap {
            if (miniAppNeedsLegacyRefreshHook(HostInfo.versionCode) &&
                ClassUtils.loadClassOrNull(NEW_HEADER) != null
            ) {
                put("refreshStateChanged", FindMethod().apply {
                    matcher {
                        declaredClass(NEW_HEADER)
                        paramCount = 3
                        usingStrings("refreshLayout", "oldState", "newState")
                    }
                })
            }
        }
    }

    private const val NEW_HEADER = "com.tencent.qqnt.chats.view.MiniOldStyleHeaderNew"
    private const val OLD_HEADER = "com.tencent.qqnt.chats.view.MiniOldStyleHeader"
}

// Keep a short pull gesture when the mini app panel is hidden.
internal fun limitMiniAppPullOffset(offset: Int, density: Float): Int =
    offset.coerceAtMost((60 * density).toInt())

internal class MiniAppHeaderState(private val enableTwoLevel: Field) {
    private val originalValues = WeakHashMap<Any, Boolean>()

    @Synchronized
    fun apply(header: Any, hidden: Boolean) {
        if (hidden) {
            originalValues.getOrPut(header) { enableTwoLevel.getBoolean(header) }
            enableTwoLevel.setBoolean(header, false)
        } else {
            originalValues.remove(header)?.let { enableTwoLevel.setBoolean(header, it) }
        }
    }
}

internal fun miniAppTwoLevelFieldName(versionCode: Long, legacyHeader: Boolean): String = when {
    legacyHeader -> "D"
    versionCode >= 15390 -> "t"
    versionCode >= 9898 -> "I"
    versionCode >= 8538 -> "E"
    else -> "D"
}

internal fun miniAppNeedsLegacyRefreshHook(versionCode: Long): Boolean = versionCode < 15390

internal fun findMiniAppContentField(header: Class<*>): Field {
    val owner = generateSequence(header) { it.superclass }
        .firstOrNull { it.simpleName == "QQChatListTwoLevelHeader" } ?: header
    return owner.declaredFields.single {
        it.type == RelativeLayout::class.java && !Modifier.isStatic(it.modifiers)
    }.apply { isAccessible = true }
}

internal fun findMiniAppMovingCallbacks(header: Class<*>): List<Method> =
    generateSequence(header) { it.superclass }.flatMap { it.declaredMethods.asSequence() }
        .filter {
            val params = it.parameterTypes
            it.returnType == Void.TYPE && !Modifier.isStatic(it.modifiers) &&
                !Modifier.isAbstract(it.modifiers) && params.size == 6 && !params[0].isPrimitive &&
                params.drop(1) == listOf(
                    Boolean::class.javaPrimitiveType, Float::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
                )
        }.distinct().toList()

internal fun findMiniAppTwoLevelField(header: Class<*>, versionCode: Long, legacyHeader: Boolean): Field {
    val owner = generateSequence(header) { it.superclass }.firstOrNull { it.simpleName == "TwoLevelHeader" }
        ?: throw ClassNotFoundException("TwoLevelHeader")
    return owner.getDeclaredField(miniAppTwoLevelFieldName(versionCode, legacyHeader)).apply {
        check(type == Boolean::class.javaPrimitiveType && !Modifier.isStatic(modifiers)) {
            "Unexpected mini app enable flag: $this"
        }
        isAccessible = true
    }
}
