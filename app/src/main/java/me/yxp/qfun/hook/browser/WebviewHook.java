package me.yxp.qfun.hook.browser;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;

@HookItemAnnotation(TAG = "解除风险网页拦截", desc = "点击消息中链接时不再拦截风险网页", TargetProcess = ":tool")
public final class WebviewHook extends BaseSwitchHookItem {
    private static Method sLiftLimitMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        sLiftLimitMethod = DexKit.getMethod(getNAME());
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sLiftLimitMethod, null, param -> param.setResult(true));
    }
}