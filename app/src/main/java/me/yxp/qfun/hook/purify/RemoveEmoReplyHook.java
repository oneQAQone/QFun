package me.yxp.qfun.hook.purify;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;

@HookItemAnnotation(TAG = "移除表情回应", desc = "长按消息时不再显示回应表情的菜单")
public final class RemoveEmoReplyHook extends BaseSwitchHookItem {

    private static Method needShow;

    @Override
    protected boolean initMethod() throws Throwable {
        needShow = DexKit.getMethod(getNAME());
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.replaceIfEnable(this, needShow, param -> false);
    }
}
