package me.yxp.qfun.hook.file

import com.tencent.mobileqq.aio.panel.photo.PhotoPanelVB
import com.tencent.mvi.base.mvi.MviUIState
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "自动勾选原图",
    "发送图片时自动勾选原图（半屏相册）",
    HookCategory.FILE
)
object AutoSendOriginalPic : BaseSwitchHookItem() {

    private lateinit var handleUIState: Method

    private lateinit var setChecked: Method

    override fun onInit(): Boolean {
        handleUIState = PhotoPanelVB::class.java
            .findMethod {
                returnType = void
                paramTypes(MviUIState::class.java)
            }
        setChecked = PhotoPanelVB::class.java
            .findMethod {
                visibility = public
                returnType = void
                paramTypes(boolean)
            }
        return super.onInit()
    }

    override fun onHook() {
        handleUIState.hookAfter(this) {
            setChecked.invoke(it.thisObject, true)
        }
    }

}