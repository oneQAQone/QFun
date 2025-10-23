package me.yxp.qfun.hook.base;

import android.content.Context;
import android.content.SharedPreferences;

import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "基础开关项")
public abstract class BaseSwitchHookItem {
    private static final String SHARED_PREFS_NAME = "QFun_Config";
    private static final String TARGET_PROCESS_ALL = "All";

    public boolean isAvailable;

    public final void init() {
        try {
            isAvailable = initMethod();
            if (isAvailable && isInTargetProcess()) {
                initCallback();
            }
        } catch (Throwable th) {
            ErrorOutput.itemHookError(this, th);
            isAvailable = false;
        }
    }

    private HookItemAnnotation getAnnotation() {
        return this.getClass().getAnnotation(HookItemAnnotation.class);
    }

    public final boolean isInTargetProcess() {
        if (getAnnotation().TargetProcess().equals(TARGET_PROCESS_ALL)) {
            return true;
        }
        return (HostInfo.getPackageName() + getAnnotation().TargetProcess()).equals(SyncUtils.getProcessName());
    }

    public final String getTAG() {
        return getAnnotation().TAG();
    }

    public final String getDESC() {
        return getAnnotation().desc();
    }

    public final String getNAME() {
        return this.getClass().getSimpleName();
    }

    public void startHook() {
        SharedPreferences sp = HostInfo.getHostContext().getSharedPreferences(
                SHARED_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        sp.edit().putBoolean(getNAME(), true).apply();
    }

    public void stopHook() {
        SharedPreferences sp = HostInfo.getHostContext().getSharedPreferences(
                SHARED_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        sp.edit().putBoolean(getNAME(), false).apply();
    }

    public final boolean getHookStatus() {
        SharedPreferences sp = HostInfo.getHostContext().getSharedPreferences(
                SHARED_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        return sp.getBoolean(getNAME(), false);
    }

    protected boolean initMethod() throws Throwable {
        return true;
    }

    protected abstract void initCallback();
}