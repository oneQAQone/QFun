package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method

@HookItemAnnotation(
    "去除滤镜视频",
    "去除新版QQ底部选项中的滤镜视频",
    HookCategory.PURIFY
)
object RemoveFilterVideo : BaseSwitchHookItem(), DexKitTask {

    private lateinit var addFilter: Method

    override fun onInit(): Boolean {
        if (HostInfo.isTIM || (HostInfo.isQQ && HostInfo.versionCode < 11310)) return false
        addFilter = requireClass("addFilter")
            .findMethod {
                returnType = void
                paramTypes(list, null)
            }
        return super.onInit()
    }

    override fun onHook() {
        addFilter.doNothing(this)
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "addFilter" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.shortcurtbar")
            matcher {
                usingStrings("originList", "filterVideoItem")
            }
        }
    )

}