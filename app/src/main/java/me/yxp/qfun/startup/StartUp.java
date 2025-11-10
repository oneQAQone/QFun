package me.yxp.qfun.startup;

import android.content.Context;

import me.yxp.qfun.lifecycle.Parasitics;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.qq.HostInfo;

public class StartUp {

    public static void hookApplicationCreate(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.tencent.common.app.BaseApplicationImpl",
                classLoader, "onCreate", new XC_MethodHook() {

                    private boolean mIsFirst = true;

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!mIsFirst) {
                            return;
                        }
                        mIsFirst = false;
                        Context hostContext = (Context) param.thisObject;
                        HostInfo.setHostInfo(hostContext);
                        Parasitics.initForStubActivity(hostContext);
                        Parasitics.injectModuleResources(hostContext.getResources());
                        DexKit.checkVersion();
                        ErrorOutput.recordEnvironmentInfo();

                    }
                });
    }
}