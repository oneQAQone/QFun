package me.yxp.qfun.hook.troop

import com.tencent.mobileqq.aio.input.at.common.SubmitListEvent
import com.tencent.qqnt.kernel.nativeinterface.MemberInfo
import com.tencent.qqnt.kernelpublic.nativeinterface.MemberRole
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull

@HookItemAnnotation(
    "艾特列表排序",
    "按群主，管理，官机，成员排序艾特列表",
    HookCategory.GROUP
)
object SortingAtList : BaseSwitchHookItem() {

    override fun onHook() {
        SubmitListEvent::class.java
            .findMethod {
                name = "getItemList"
            }.hookAfter(this) { param ->
                val itemList = param.result as List<*>
                val newList = itemList.sortedBy {
                    val memberInfo = it?.getObjectByTypeOrNull<MemberInfo>() ?: return@sortedBy 0
                    if (memberInfo.isRobot) return@sortedBy 3
                    return@sortedBy when (memberInfo.role) {
                        MemberRole.OWNER -> 1
                        MemberRole.ADMIN -> 2
                        MemberRole.MEMBER -> 4
                        else -> 5
                    }
                }
                param.result = newList
            }
    }

}