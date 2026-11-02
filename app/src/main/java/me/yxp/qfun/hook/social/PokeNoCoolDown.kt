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
package me.yxp.qfun.hook.social

import com.tencent.mobileqq.paiyipai.PaiYiPaiHandler
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "去除戳一戳冷却时间",
    "去除戳一戳的本地冷却时间，无需开启拍一拍连拍",
    HookCategory.SOCIAL
)
object PokeNoCoolDown : BaseSwitchHookItem() {

    private lateinit var canSendReq: Method

    override fun onInit(): Boolean {
        if (!HostInfo.isQQ) return false
        // Locate QQ's local cooldown check before sending a poke request.
        canSendReq = PaiYiPaiHandler::class.java.findMethod {
            returnType = boolean
            paramTypes(string)
        }
        return true
    }

    override fun onHook() {
        canSendReq.hookBefore(this) { it.result = true }
    }
}
