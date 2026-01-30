package me.yxp.qfun.hook.purify

import android.annotation.SuppressLint
import android.widget.ImageView
import com.tencent.qqnt.aio.shortcutbar.PanelIconLinearLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "去除滤镜视频",
    "去除新版QQ底部选项中的滤镜视频",
    HookCategory.PURIFY
)
object RemoveFilterVideo : BaseSwitchHookItem() {

    private lateinit var bindView: Method

    override fun onInit(): Boolean {
        if (HostInfo.isTIM || (HostInfo.isQQ && HostInfo.versionCode < 11310)) return false
        bindView = PanelIconLinearLayout::class.java
            .findMethod {
                returnType = void
                paramTypes(int, string, null)
            }
        return super.onInit()
    }

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        bindView.hookAfter(this) {
            val layout = it.thisObject as PanelIconLinearLayout
            val icon = layout.findViewWithTag<ImageView>(1016) ?: return@hookAfter


            val id = HostInfo.hostContext.resources
                .getIdentifier(
                    "qui_red_envelope_aio_oversized_light_selector",
                    "drawable",
                    HostInfo.packageName
                )

            icon.tag = 1004
            icon.contentDescription = "红包"
            icon.setImageResource(id)

        }
    }

}