package me.yxp.qfun.utils.qq;

import android.app.Activity;

import java.lang.reflect.Field;
import java.util.Map;

import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.reflect.ClassUtils;

public class QQCurrentEnv {
    private static String sCurrentUin;
    private static Object sKernelMsgService;
    private static Object sQQAppInterface;

    public static Activity getActivity() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);

                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Object getKernelMsgservice() {
        if (sKernelMsgService == null) setKernelMsgService();
        return sKernelMsgService;
    }

    private static void setCurrentUin() {
        try {
            sCurrentUin = (String) XposedHelpers.callMethod(sQQAppInterface, "getCurrentAccountUin");
        } catch (Exception ignored) {
        }
    }

    private static void setKernelMsgService() {
        try {
            Class<?> iKernelIService = ClassUtils.load("com.tencent.qqnt.kernel.api.IKernelService");
            Object kernelService = XposedHelpers.callMethod(sQQAppInterface, "getRuntimeService", iKernelIService, "");
            Object msgService = XposedHelpers.callMethod(kernelService, "getMsgService");
            sKernelMsgService = XposedHelpers.callMethod(msgService, "getService");
        } catch (Throwable ignored) {
        }
    }

    public static String getCurrentUin() {
        return (sCurrentUin == null || sCurrentUin.isEmpty()) ? "global" : sCurrentUin;
    }

    public static String getCurrentNickname() {
        try {
            return (String) XposedHelpers.callMethod(sQQAppInterface, "getCurrentNickname");
        } catch (Exception e) {
            return "";
        }
    }

    public static Object getQQAppInterface() {
        return sQQAppInterface;
    }

    public static void setCurrentEnv(Object qqAppInterface) {
        sQQAppInterface = qqAppInterface;
        setCurrentUin();
        setKernelMsgService();
    }
}