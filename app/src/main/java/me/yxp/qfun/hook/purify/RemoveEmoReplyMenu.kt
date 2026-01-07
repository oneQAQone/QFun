package me.yxp.qfun.hook.purify

import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msglist.holder.component.msgtail.AIOGeneralMsgTailContentComponent
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.findMethod
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method

@HookItemAnnotation(
    "移除表情回应",
    "移除长按出现的表情回应菜单以及消息下方表情回应",
    HookCategory.PURIFY
)
object RemoveEmoReplyMenu : BaseSwitchHookItem(), DexKitTask {

    private lateinit var handleUIState: Method

    override fun onInit(): Boolean {
        handleUIState = AIOGeneralMsgTailContentComponent::class.java
            .findMethod {
                returnType = void
                paramTypes(int, AIOMsgItem::class.java, list)
            }
        return super.onInit()
    }

    override fun onHook() {
        requireMethod("check").returnConstant(this, false)
        handleUIState.doNothing(this)
    }


    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "check" to FindMethod().apply {
            searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.msgtail")
            matcher {
                usingStrings("chatType is not group")
            }
        }
    )

}