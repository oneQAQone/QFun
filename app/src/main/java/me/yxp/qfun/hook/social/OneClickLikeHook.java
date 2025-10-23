package me.yxp.qfun.hook.social;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "一键点赞", desc = "一次性点赞至每天上限次数")
public final class OneClickLikeHook extends BaseSwitchHookItem {
    private static Method sSendZanMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> cardHandler = ClassUtils._CardHandler();
        sSendZanMethod = MethodUtils.create(cardHandler)
                .withReturnType(void.class)
                .withParamTypes(long.class, long.class, byte[].class, int.class, int.class, int.class)
                .findOne();
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sSendZanMethod, param -> {
            param.setResult(null);
            param.args[4] = 10;
            for (int i = 0; i < 5; i++) {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        }, null);
    }
}