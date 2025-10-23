package me.yxp.qfun.utils.qq;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class HostInfo {
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";

    private static long sVersionCode;
    private static String sModuleDataPath;
    private static Context sHostContext;

    public static void setHostInfo(Context context) {
        setHostContext(context);
        setVersionCode(context);
        setModuleDataPath(context);
    }

    public static long getVersionCode() {
        return sVersionCode;
    }

    private static void setVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sVersionCode = packageInfo.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    public static String getPackageName() {
        return sHostContext.getPackageName();
    }

    public static boolean isQQ() {
        return PACKAGE_NAME_QQ.equals(getPackageName());
    }

    public static boolean isTIM() {
        return PACKAGE_NAME_TIM.equals(getPackageName());
    }

    public static String getMODULE_DATA_PATH() {
        return sModuleDataPath;
    }

    private static void setModuleDataPath(Context context) {
        sModuleDataPath = context.getExternalFilesDir(null).getParent() + "/QFun/";
    }

    public static Context getHostContext() {
        return sHostContext;
    }

    private static void setHostContext(Context context) {
        sHostContext = context;
    }

    public static boolean isInHostProcess() {
        if (sHostContext == null) return false;
        String packageName = getPackageName();
        return PACKAGE_NAME_QQ.equals(packageName) || PACKAGE_NAME_TIM.equals(packageName);
    }
}