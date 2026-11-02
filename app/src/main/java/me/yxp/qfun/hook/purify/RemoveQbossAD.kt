/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
/*
 * Modified for QFun on 2026-11-02: configuration, package and hook integration.
 */
package me.yxp.qfun.hook.purify

import android.view.View
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.reflect.ClassUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

@HookItemAnnotation(
    "移除消息列表顶栏横幅广告",
    "移除消息列表顶部的横幅广告",
    HookCategory.PURIFY
)
object RemoveQbossAD : BaseSwitchHookItem() {

    private var bannerMethods: List<Method> = emptyList()

    override fun onInit(): Boolean {
        val manager = findBannerManager() ?: return false
        bannerMethods = manager.declaredMethods.filter {
            it.returnType == View::class.java &&
                it.parameterCount == 0 && !Modifier.isStatic(it.modifiers)
        }
        return bannerMethods.isNotEmpty()
    }

    override fun onHook() {
        bannerMethods.forEach { method ->
            method.hookBefore(this) { it.result = null }
        }

        // Some versions still process banner messages after the view is removed.
        // Keep normal processing, but contain exceptions caused by the missing view.
        val processor = ClassUtils.loadClassOrNull(
            "com.tencent.mobileqq.activity.recent.bannerprocessor.VasADBannerProcessor"
        ) ?: return
        processor.declaredMethods.filter { it.name == "handleMessage" }.forEach { method ->
            runCatching {
                method.hookReplace(this) { chain ->
                    try {
                        chain.proceed()
                    } catch (e: Exception) {
                        LogUtils.e(this, e)
                        if (method.returnType == Boolean::class.javaPrimitiveType) true else null
                    }
                }
            }.onFailure { LogUtils.e(this, it) }
        }
    }

    private fun findBannerManager(): Class<*>? {
        val classNames = listOf(
            "cooperation.vip.qqbanner.QbossADImmersionBannerManager",
            "cooperation.vip.qqbanner.manager.VasADImmersionBannerManager"
        )
        for (name in classNames) {
            ClassUtils.loadClassOrNull(name)?.let { return it }
            // Recover a renamed outer class from its anonymous inner class.
            for (index in 1..2) {
                val inner = ClassUtils.loadClassOrNull("$name\$$index") ?: continue
                runCatching { inner.getDeclaredField("this\$0").type }
                    .getOrNull()?.let { return it }
            }
        }
        return null
    }
}
