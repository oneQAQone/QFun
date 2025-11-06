package me.yxp.qfun.hook.browser;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "解除风险网页拦截", desc = "点击消息中链接时不再拦截风险网页", TargetProcess = ":tool")
public final class RemoveRiskWebpageBlockHook extends BaseSwitchHookItem {
    private static Method sLoadUrl;

    private String targetUrl;

    @Override
    protected boolean initMethod() throws Throwable {

        sLoadUrl = MethodUtils.create(ClassUtils._CustomWebView())
                .withMethodName("loadUrl")
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.replaceIfEnable(this, sLoadUrl, param -> {
            String url = (String) param.args[0];
            if (url.contains("c.pc.qq.com")) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, new Object[]{targetUrl});
            } else {
                targetUrl = url;
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });
    }
}