package me.yxp.qfun.hook.qrcode;

import android.os.Bundle;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "解除扫码限制", desc = "解除长按识别或从相册中扫描二维码时的风险检查")
public final class LiftQRCodeLimitHook extends BaseSwitchHookItem {
    private static Method sCheckMethod;

    @Override
    protected boolean initMethod() throws Throwable {

        try {
            sCheckMethod = MethodUtils.create(ClassUtils._QrAgentLoginManager())
                    .withReturnType(void.class)
                    .withParamTypes(boolean.class, String.class, Bundle.class)
                    .findOne();
        } catch (Exception e) {
            sCheckMethod = MethodUtils.create(ClassUtils._QrAgentLoginManager())
                    .withReturnType(void.class)
                    .withParamTypes(ClassUtils._QrAgentLoginManager(), boolean.class, String.class, Bundle.class)
                    .findOne();
        }

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sCheckMethod,
                param -> {
                    Object args = param.args;
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof Boolean) {
                            args[i] = false;
                        }
                    }
                }, null);

    }
}