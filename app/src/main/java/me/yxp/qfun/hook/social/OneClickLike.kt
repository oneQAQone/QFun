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
package me.yxp.qfun.hook.social

import android.view.View
import android.widget.ImageView
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.callOriginal
import me.yxp.qfun.utils.reflect.findField
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.toClass
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

@HookItemAnnotation(
    "一键20赞",
    "点击资料卡或回赞页面的点赞按钮时连续点赞",
    HookCategory.SOCIAL
)
object OneClickLike : BaseSwitchHookItem() {

    private lateinit var cardProfileClass: Class<*>
    private lateinit var voteHelperField: Field
    private lateinit var onClickVote: Method
    private lateinit var profileVoteClick: Method
    private var visitorClicks: List<Method> = emptyList()

    override fun onInit(): Boolean {
        if (!HostInfo.isQQ) return false

        cardProfileClass = "com.tencent.mobileqq.data.CardProfile".toClass
        val voteHelperClass = findVoteHelper()
        onClickVote = voteHelperClass.declaredMethods.single {
            !Modifier.isStatic(it.modifiers) &&
                it.parameterTypes.contentEquals(arrayOf(cardProfileClass, ImageView::class.java))
        }.apply { isAccessible = true }

        val visitorsClass = "com.tencent.mobileqq.activity.VisitorsActivity".toClass
        voteHelperField = visitorsClass.findField {
            type = voteHelperClass
            isStatic = false
        }
        visitorClicks = visitorsClass.findMethods {
            name = "onClick"
            paramTypes(View::class.java)
        }
        check(visitorClicks.isNotEmpty()) { "VisitorsActivity.onClick(View) not found" }
        profileVoteClick = "com.tencent.mobileqq.profilecard.base.component.AbsProfileHeaderComponent".toClass
            .getDeclaredMethod(
                "handleVoteBtnClickForGuestProfile",
                "com.tencent.mobileqq.data.Card".toClass
            ).apply { isAccessible = true }
        return true
    }

    override fun onHook() {

        visitorClicks.forEach { method ->
            method.hookBefore(this) { param ->
                val view = param.args[0] as? View ?: return@hookBefore
                val profile = view.tag ?: return@hookBefore
                if (!cardProfileClass.isInstance(profile)) return@hookBefore
                val voteHelper = voteHelperField.get(param.thisObject) ?: return@hookBefore
                // Add 20 vote operations, then let the original click continue.
                repeat(20) { onClickVote.invoke(voteHelper, profile, view) }
            }
        }

        profileVoteClick.hookBefore(this) { param ->
            // The original click supplies the twentieth operation.
            repeat(19) { param.method.callOriginal(param.thisObject, *param.args) }
        }
    }

    private fun findVoteHelper(): Class<*> {
        val name = "com.tencent.mobileqq.profile.vote.VoteHelper"
        ClassUtils.loadClassOrNull(name)?.let { return it }
        for (index in listOf(1, 3, 4)) {
            val inner = ClassUtils.loadClassOrNull("$name\$$index") ?: continue
            runCatching { inner.getDeclaredField("this\$0").type }
                .getOrNull()?.let { return it }
        }
        throw ClassNotFoundException(name)
    }
}
