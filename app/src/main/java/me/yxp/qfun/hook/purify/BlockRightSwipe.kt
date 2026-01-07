package me.yxp.qfun.hook.purify

import com.tencent.aio.frame.drawer.DrawerFrameViewGroup
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.replaceFirstParam
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "屏蔽聊天右滑",
    "屏蔽聊天界面右滑显示的界面",
    HookCategory.PURIFY
)
object BlockRightSwipe : BaseSwitchHookItem() {

    private lateinit var index: Method

    override fun onInit(): Boolean {
        if (HostInfo.isTIM) return false
        index = DrawerFrameViewGroup::class.java
            .findMethod {
                visibility = private
                returnType = void
                paramTypes(int)
            }
        return super.onInit()
    }

    override fun onHook() {
        index.replaceFirstParam(0, this)
    }

}