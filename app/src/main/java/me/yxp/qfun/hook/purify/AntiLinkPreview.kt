package me.yxp.qfun.hook.purify

import com.tencent.mobileqq.aio.msg.TextMsgContent
import com.tencent.qqnt.kernel.nativeinterface.LinkInfo
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "屏蔽链接预览",
    "屏蔽链接文本的预览信息",
    HookCategory.PURIFY
)
object AntiLinkPreview : BaseSwitchHookItem() {

    private lateinit var addPreview: Method

    override fun onInit(): Boolean {
        addPreview = TextMsgContent::class.java
            .findMethod {
                returnType = void
                paramTypes(null, LinkInfo::class.java)
            }
        return super.onInit()
    }

    override fun onHook() {
        addPreview.doNothing(this)
    }
}