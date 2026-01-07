package me.yxp.qfun.hook.device

import androidx.compose.runtime.Composable
import com.tencent.qmethod.pandoraex.monitor.DeviceInfoMonitor
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.DeviceConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.CustomDevicePage
import me.yxp.qfun.utils.hook.returnConstant

@HookItemAnnotation(
    "伪装设备在线状态",
    "点击设置机型，可用于设置在线状态机型（包含文字可能无效，重启生效）",
    HookCategory.DEVICE,
    "All"
)
object CustomDevice : BaseClickableHookItem<DeviceConfig>(DeviceConfig.serializer()) {

    override val defaultConfig: DeviceConfig = DeviceConfig()

    override fun onHook() {
        DeviceInfoMonitor::class.java
            .getDeclaredMethod("getModel")
            .returnConstant(this, config.fakeModel)
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        CustomDevicePage(
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }
}
