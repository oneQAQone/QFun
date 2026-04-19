package me.yxp.qfun.hook.purify

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.compose.runtime.Composable
import com.tencent.qqnt.aio.shortcutbar.PanelIconLinearLayout
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.ShortcutConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.ShortcutBarPage
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "快捷栏精简",
    "自定义快捷按钮显示，支持将泡泡位置替换为红包",
    HookCategory.PURIFY
)
object ShortcutBarCustom : BaseClickableHookItem<ShortcutConfig>(ShortcutConfig.serializer()) {

    override val defaultConfig = ShortcutConfig()

    private lateinit var bindView: Method

    private val allPhysicalIds = listOf(1000, 1001, 1003, 1004, 1005, 1006, 1016)

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        bindView = PanelIconLinearLayout::class.java
            .findMethod {
                returnType = void
                paramTypes(int, string, null)
            }
        return super.onInit()
    }

    override fun onHook() {
        bindView.hookAfter(this) { param ->
            val layout = param.thisObject as PanelIconLinearLayout

            allPhysicalIds.forEach { tag ->

                val iconView = layout.findViewWithTag<ImageView>(tag) ?: return@forEach

                if (tag == 1016 && config.visibleIds.contains(1004)) transformToRedPacket(iconView)
                if (!config.visibleIds.contains(iconView.tag as Int)) layout.removeView(iconView)

            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun transformToRedPacket(view: ImageView) {
        val resId = HostInfo.hostContext.resources.getIdentifier(
            "qui_red_envelope_aio_oversized_light_selector",
            "drawable",
            HostInfo.packageName
        )

        view.setImageResource(resId)
        view.contentDescription = "红包"
        view.tag = 1004
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        ShortcutBarPage(config, ::updateConfig, onDismiss)
    }
}
