package me.yxp.qfun.lifecycle;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import me.yxp.qfun.utils.qq.HostInfo;

public class CounterfeitActivityInfoFactory {

    public static ActivityInfo makeProxyActivityInfo(String className, long flags) {
        try {
            Context ctx = HostInfo.getHostContext();
            Class<?> cl = Class.forName(className);
            String[] candidates = new String[]{"com.tencent.mobileqq.activity.QQSettingSettingActivity",
                    "com.tencent.mobileqq.activity.QPublicFragmentActivity"};
            PackageManager.NameNotFoundException last = null;
            for (String activityName : candidates) {
                try {

                    ActivityInfo proto = ctx.getPackageManager()
                            .getActivityInfo(new ComponentName(ctx.getPackageName(), activityName), (int) flags);

                    return initCommon(proto, className);
                } catch (PackageManager.NameNotFoundException e) {
                    last = e;
                }
            }
            throw new IllegalStateException("QQSettingSettingActivity not found, are we in the host?", last);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static ActivityInfo initCommon(ActivityInfo ai, String name) {
        ai.targetActivity = null;
        ai.taskAffinity = null;
        ai.descriptionRes = 0;
        ai.name = name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ai.splitName = null;
        }
        ai.configChanges |= ActivityInfo.CONFIG_UI_MODE;
        return ai;
    }
}

