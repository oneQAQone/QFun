package me.yxp.qfun.hook.qrcode;

import android.os.Bundle;
import android.widget.Button;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "解除扫码限制", desc = "解除长按识别或从相册中扫描二维码时的风险检查，以及跳过确定倒计时")
public final class LiftQRCodeLimitHook extends BaseSwitchHookItem {
    private static Method sCheckMethod;
    private static Method sSetEnableMethod;
    private static Class<?> sQUIButton;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> qrAgentLoginManager = ClassUtils.load("com.tencent.open.agent.QrAgentLoginManager");
        Class<?> qrLoginAuthActivity = ClassUtils.load("com.tencent.biz.qrcode.activity.QRLoginAuthActivity");
        sQUIButton = ClassUtils.load("com.tencent.biz.qui.quibutton.QUIButton");

        sCheckMethod = MethodUtils.create(qrAgentLoginManager)
                .withReturnType(void.class)
                .withParamTypes(boolean.class, String.class, Bundle.class)
                .findOne();

        sSetEnableMethod = MethodUtils.create(qrLoginAuthActivity)
                .withAccessModifier(MethodUtils.AccessModifier.PRIVATE)
                .withReturnType(void.class)
                .withParamTypes(String.class)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sCheckMethod,
                param -> param.args[0] = false, null);

        HookUtils.replaceIfEnable(this, sSetEnableMethod, param -> {
            Button confirmButton = (Button) FieldUtils.create(param.thisObject)
                    .ofType(sQUIButton).getValue();
            confirmButton.setEnabled(true);
            return null;
        });
    }
}