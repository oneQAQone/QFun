package me.yxp.qfun.hook.msg

import com.tencent.qqnt.aio.adapter.api.impl.RichMediaBrowserApiImpl
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    "以图片方式打开表情",
    "将表情包作为普通图片打开，方便下载",
    HookCategory.MSG
)
object EmotionToPic : BaseSwitchHookItem() {

    override fun onHook() {
        RichMediaBrowserApiImpl::class.java
            .findMethod {
                name = "checkIsFavPicAndShowPreview"
            }
            .returnConstant(this, false)
    }
}