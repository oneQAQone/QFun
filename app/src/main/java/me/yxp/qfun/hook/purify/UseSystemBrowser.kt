/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
/*
 * Modified for QFun on 2026-11-02: configuration, package and hook integration.
 * Additional reference: StartActivityHook, Copyright (C) 2019-2022 qwq233@qwq2333.top.
 */
package me.yxp.qfun.hook.purify

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.net.toUri
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo

@HookItemAnnotation(
    "使用系统浏览器打开链接",
    "普通网页交给外部浏览器打开，QQ登录、支付等页面保留内置浏览器",
    HookCategory.PURIFY,
    "All"
)
object UseSystemBrowser : BaseSwitchHookItem() {

    private const val EXTRA_BYPASS = "me.singleneuron.hook.decorator.EXTRA_BYPASS_FQB_HOOK"

    override fun onInit() = HostInfo.isInHostProcess

    override fun onHook() {
        listOf(ContextWrapper::class.java, Activity::class.java).forEach { clazz ->
            clazz.declaredMethods.filter { method ->
                (method.name == "startActivity" || method.name == "startActivityForResult") &&
                    method.parameterTypes.contains(Intent::class.java)
            }.forEach { method ->
                method.hookBefore(this) { param ->
                    val intent = param.args.firstOrNull { it is Intent } as? Intent
                        ?: return@hookBefore
                    val component = intent.component ?: return@hookBefore
                    if (component.packageName != HostInfo.packageName ||
                        !component.className.contains("QQBrowserActivity") ||
                        intent.getBooleanExtra(EXTRA_BYPASS, false)
                    ) return@hookBefore
                    val url = intent.getStringExtra("url")?.let(::externalBrowserUrl)
                        ?: return@hookBefore
                    val externalIntent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                        addCategory(Intent.CATEGORY_BROWSABLE)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(EXTRA_BYPASS, true)
                    }
                    val context = param.thisObject as? Context ?: HostInfo.hostContext
                    // Only suppress the original launch after the external one succeeds.
                    context.startActivity(externalIntent)
                    param.result = null
                }
            }
        }
    }
}
