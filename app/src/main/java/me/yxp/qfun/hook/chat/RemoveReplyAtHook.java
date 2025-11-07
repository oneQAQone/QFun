package me.yxp.qfun.hook.chat;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "去除回复自动艾特", desc = "引用消息时不再自动添加艾特")
public final class RemoveReplyAtHook extends BaseSwitchHookItem {
    private static final String MsgOnClickReplyEvent = "com.tencent.mobileqq.aio.event.AIOMsgSendEvent$MsgOnClickReplyEvent";
    private static Method sHandleMsgIntent;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> reply = DexKit.getClass(getNAME());
        sHandleMsgIntent = MethodUtils.create(reply)
                .withReturnType(void.class)
                .withParamTypes(ClassUtils.load("com.tencent.mvi.base.route.MsgIntent"))
                .findOne();
        return true;
    }

    @Override
    public void initCallback() {
        HookUtils.replaceIfEnable(this, sHandleMsgIntent, param -> {
            if (param.args[0] == null || !MsgOnClickReplyEvent.equals(param.args[0].getClass().getName())) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
            Object aIOMsgItem = FieldUtils.create(param.args[0]).ofType(ClassUtils._AIOMsgItem()).getValue();
            Object msgRecord = FieldUtils.create(aIOMsgItem)
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();
            Object senderUid = FieldUtils.create(msgRecord).withName("senderUid").getValue();
            FieldUtils.create(msgRecord).withName("senderUid").setValue("");
            XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            //长按头像时也会调用此msgRecord，所以调用后还原
            FieldUtils.create(msgRecord).withName("senderUid").setValue(senderUid);
            return null;
        });

    }
}