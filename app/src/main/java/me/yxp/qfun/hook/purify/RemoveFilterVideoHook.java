package me.yxp.qfun.hook.purify;

import java.lang.reflect.Method;
import java.util.List;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "去除滤镜视频", desc = "去除新版QQ底部选项中的滤镜视频（QQ9.2.10（11310）及以上）")
public final class RemoveFilteredVideoHook extends BaseSwitchHookItem {
    private static Method sAddFilterVideoItem;

    @Override
    protected boolean initMethod() throws Throwable {
        if (HostInfo.isTIM() || (HostInfo.isQQ() && HostInfo.getVersionCode() < 11310)) {
            return false;
        }
        sAddFilterVideoItem = MethodUtils.create(DexKit.getClass(getNAME())).withParamTypes(void.class).withParamTypes(List.class, null).findOne();
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.replaceIfEnable(this, sAddFilterVideoItem, param -> null);
    }
}
