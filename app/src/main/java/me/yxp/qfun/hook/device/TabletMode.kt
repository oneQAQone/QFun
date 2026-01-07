package me.yxp.qfun.hook.device

import com.tencent.common.config.pad.DeviceType
import com.tencent.common.config.pad.PadUtil
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

@HookItemAnnotation(
    "平板模式",
    "让QQ将当前设备识别为平板(重启QQ生效)",
    HookCategory.DEVICE,
    ":MSF"
)
object TabletMode : BaseSwitchHookItem() {

    private lateinit var getDeviceType: Method

    override fun onInit(): Boolean {
        getDeviceType = PadUtil::class.java
            .findMethod {
                returnType = DeviceType::class.java
            }
        return super.onInit()
    }

    override fun onHook() {
        getDeviceType.returnConstant(this, DeviceType.TABLET)
    }
}