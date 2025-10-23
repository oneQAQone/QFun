package me.yxp.qfun.hook.file;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "自动勾选原图", desc = "发送图片时自动勾选原图（半屏相册）")
public final class AutoSendOriginalPicHook extends BaseSwitchHookItem {
    private static Method sHandleUIState;
    private static Method sSetChecked;

    @Override
    protected boolean initMethod() throws Throwable {

        sHandleUIState = MethodUtils.create(ClassUtils._PhotoPanelVB())
                .withReturnType(void.class)
                .withParamTypes(ClassUtils.load("com.tencent.mvi.base.mvi.MviUIState"))
                .findOne();

        sSetChecked = MethodUtils.create(ClassUtils._PhotoPanelVB())
                .withAccessModifier(MethodUtils.AccessModifier.PUBLIC)
                .withReturnType(void.class)
                .withParamTypes(boolean.class)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sHandleUIState, null,
                param -> sSetChecked.invoke(param.thisObject, true));
    }
}