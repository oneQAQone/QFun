package me.yxp.qfun.hook.msg;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "查看聊天记录回复消息", desc = "强制查看聊天记录回复消息中包含的图片消息（QQ9.1.71(9906)后添加限制，请以实际为准）")
public final class RecordsReplyMsgHook extends BaseSwitchHookItem {
    private static Method sCheckMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        if (HostInfo.getVersionCode() <= 9906) {
            return false;
        }

        sCheckMethod = MethodUtils.create(DexKit.getClass(getNAME()))
                .withReturnType(long.class)
                .withParamTypes(ClassUtils._MsgRecord(), ClassUtils._AIOMsgItem())
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sCheckMethod, null,
                param -> param.setResult(1L));
    }
}