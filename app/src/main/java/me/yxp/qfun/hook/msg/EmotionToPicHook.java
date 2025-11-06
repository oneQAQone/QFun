package me.yxp.qfun.hook.msg;

import java.lang.reflect.Method;
import java.util.ArrayList;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "以图片方式打开表情")
public final class EmotionToPicHook extends BaseSwitchHookItem {
    private static Method sSendIntentMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> picContentViewUtil = ClassUtils.load(
                "com.tencent.mobileqq.aio.msglist.holder.base.util.PicContentViewUtil");
        Class<?> MsgElement = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.MsgElement");

        sSendIntentMethod = MethodUtils.create(picContentViewUtil)
                .withReturnType(void.class)
                .withParamTypes(null, MsgElement, null, ArrayList.class, null, Runnable.class)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sSendIntentMethod, param -> {
            Object picElement = FieldUtils.create(param.args[1]).withName("picElement").getValue();
            FieldUtils.create(picElement).withName("picSubType").setValue(0);
            FieldUtils.create(picElement).withName("picType").setValue(1000);
        }, null);
    }
}