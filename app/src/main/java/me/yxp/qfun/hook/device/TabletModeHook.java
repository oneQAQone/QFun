package me.yxp.qfun.hook.device;

import android.content.Context;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "平板模式", desc = "让QQ将当前设备识别为平板(重启QQ生效)", TargetProcess = ":MSF")
public final class TabletModeHook extends BaseSwitchHookItem {
    private static Object sTabletDeviceType;
    private static Method sGetDeviceTypeMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> deviceType = ClassUtils.load("com.tencent.common.config.pad.DeviceType");
        sGetDeviceTypeMethod = MethodUtils.create(ClassUtils._PadUtil())
                .withReturnType(deviceType)
                .withParamTypes(Context.class)
                .findOne();
        sTabletDeviceType = deviceType.getDeclaredField("TABLET").get(null);
        return true;
    }

    @Override
    public void initCallback() {
        HookUtils.hookIfEnable(this, sGetDeviceTypeMethod, null,
                param -> param.setResult(sTabletDeviceType));
    }
}