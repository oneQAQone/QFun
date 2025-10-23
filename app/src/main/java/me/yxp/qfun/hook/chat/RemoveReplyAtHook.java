package me.yxp.qfun.hook.chat;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "去除回复自动艾特", desc = "引用消息时不再自动添加艾特")
public final class RemoveReplyAtHook extends BaseSwitchHookItem {
    private static Method sSetTextMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> reply = DexKit.getClass("RemoveReplyAtHook");
        sSetTextMethod = MethodUtils.create(reply)
                .withReturnType(void.class)
                .withParamTypes(ClassUtils._AIOMsgItem())
                .findOne();
        return true;
    }

    @Override
    public void initCallback() {
        HookUtils.replaceIfEnable(this, sSetTextMethod, param -> null);
    }
}