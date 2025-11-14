package me.yxp.qfun.utils.qq;

import android.app.Activity;
import android.widget.Toast;

import java.lang.reflect.Method;

import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class ToastUtils {

    private static Method sShowQQToastInUiThread;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("", th);
        }
    }

    private static void initMethod() throws Throwable {
        Class<?> qqToastUtil = ClassUtils.load("com.tencent.util.QQToastUtil");
        sShowQQToastInUiThread = MethodUtils.create(qqToastUtil)
                .withMethodName("showQQToastInUiThread")
                .findOne();
    }

    public static void Toast(String message) {
        SyncUtils.runOnUiThread(() -> {
            Activity activity = QQCurrentEnv.getActivity();
            if (activity != null) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void QQToast(int icon, String message) {
        try {
            sShowQQToastInUiThread.invoke(null, icon, message);
        } catch (Exception ignored) {
            // 忽略异常，保持静默
        }
    }

}
