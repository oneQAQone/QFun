package me.yxp.qfun.hook.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "伪装设备在线状态", desc = "点击设置机型，可用于设置在线状态机型（包含文字可能无效，重启生效）", TargetProcess = "All")
public final class CustomDeviceHook extends BaseWithDataHookItem {
    private static final String SHARED_PREFS_NAME = "QFun_Config";
    private static final String KEY_FAKE_MODEL = "FAKE_MODEL";

    private static Method sGetModelMethod;
    private String mFakeModel;

    @Override
    public void initData() {
        SharedPreferences sp = HostInfo.getHostContext().getSharedPreferences(
                SHARED_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        mFakeModel = sp.getString(KEY_FAKE_MODEL, "");
    }

    @Override
    public void savaData() {
        SharedPreferences sp = HostInfo.getHostContext().getSharedPreferences(
                SHARED_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        sp.edit().putString(KEY_FAKE_MODEL, mFakeModel).apply();
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        final EditText editText = new EditText(context);

        if (mFakeModel != null) {
            editText.setText(mFakeModel);
        }

        new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("设备信息")
                .setView(editText)
                .setPositiveButton("确定", (dialogInterface, i) -> mFakeModel = editText.getText().toString())
                .show();
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sGetModelMethod, param -> {
            initData();
            param.setResult(mFakeModel);
        }, null);
    }

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> deviceInfoMonitor = ClassUtils.load("com.tencent.qmethod.pandoraex.monitor.DeviceInfoMonitor");
        sGetModelMethod = MethodUtils.create(deviceInfoMonitor)
                .withMethodName("getModel")
                .findOne();
        return true;
    }
}