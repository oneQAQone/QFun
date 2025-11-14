package me.yxp.qfun.utils.error;

import android.os.Build;
import android.util.Log;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.yxp.qfun.BuildConfig;
import me.yxp.qfun.common.ModuleLoader;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.loader.hookapi.IHookBridge;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.HostInfo;

public class ErrorOutput {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void Error(String tag, Throwable throwable) {
        logError("[QFun]" + tag, throwable);
    }

    public static void itemHookError(BaseSwitchHookItem hookItem, Throwable throwable) {
        String tag = hookItem.getClass().getSimpleName();
        logError("[QFun]" + tag, throwable);
    }

    private static void logError(String prefix, Throwable throwable) {
        String errorText = Log.getStackTraceString(throwable);
        String fullMessage = prefix + ":\n" + errorText;

        // 输出到Xposed日志
        XposedBridge.log(prefix);
        XposedBridge.log(errorText);

        // 写入文件
        writeErrorToFile(fullMessage);
    }

    private static void writeErrorToFile(String errorMessage) {
        try {
            String date = DATE_FORMAT.format(new Date());
            FileWriter writer = new FileWriter(DataUtils.createFile("log", "error.txt"), true);
            writer.write(date + "\n" + errorMessage + "\n\n\n");
            writer.close();
        } catch (Exception e) {
            // 忽略文件写入异常
        }
    }

    private static StringBuilder buildHeaderInfo() {
        StringBuilder header = new StringBuilder();
        header.append("=== Environment Information ===\n")
                .append("Record Time: ").append(DATE_FORMAT.format(new Date())).append("\n\n");
        return header;
    }

    private static StringBuilder buildDeviceInfo() {
        StringBuilder deviceInfo = new StringBuilder();
        deviceInfo.append("--- Device Information ---\n")
                .append("Brand: ").append(Build.BRAND).append("\n")
                .append("Model: ").append(Build.MODEL).append("\n")
                .append("Device: ").append(Build.DEVICE).append("\n")
                .append("Product: ").append(Build.PRODUCT).append("\n")
                .append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
                .append("Android Version: ").append(Build.VERSION.RELEASE).append("\n")
                .append("API Level: ").append(Build.VERSION.SDK_INT).append("\n")
                .append("Build ID: ").append(Build.ID).append("\n")
                .append("Fingerprint: ").append(Build.FINGERPRINT).append("\n")
                .append("Supported ABIs: ");

        for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
            deviceInfo.append(Build.SUPPORTED_ABIS[i]);
            if (i < Build.SUPPORTED_ABIS.length - 1) {
                deviceInfo.append(", ");
            }
        }
        deviceInfo.append("\n\n");

        return deviceInfo;
    }

    private static StringBuilder buildXposedInfo(String frameworkName, String frameworkVersion,
                                                 long frameworkVersionCode, int apiVersion) {
        StringBuilder xposedInfo = new StringBuilder();
        xposedInfo.append("--- Xposed Framework Information ---\n")
                .append("Framework Name: ").append(frameworkName).append("\n")
                .append("Framework Version: ").append(frameworkVersion).append("\n")
                .append("Framework Version Code: ").append(frameworkVersionCode).append("\n")
                .append("API Version: ").append(apiVersion).append("\n\n");
        return xposedInfo;
    }

    private static StringBuilder buildHostAppInfo(String hostPackageName,

                                                  String hostVersionName,
                                                  long hostVersionCode) {
        StringBuilder hostInfo = new StringBuilder();
        hostInfo.append("--- Host Application Information ---\n")
                .append("Package Name: ").append(hostPackageName).append("\n")
                .append("Version Name: ").append(hostVersionName).append("\n")
                .append("Version Code: ").append(hostVersionCode).append("\n\n");
        return hostInfo;
    }

    private static StringBuilder buildModuleInfo() {
        StringBuilder moduleInfo = new StringBuilder();
        moduleInfo.append("--- Module Information ---\n")
                .append("Module Version Name: ").append(BuildConfig.VERSION_NAME).append("\n")
                .append("Module Version Code: ").append(BuildConfig.VERSION_CODE).append("\n");
        return moduleInfo;
    }

    private static StringBuilder buildFooterInfo() {
        StringBuilder footer = new StringBuilder();
        footer.append("======================================\n\n");
        return footer;
    }

    public static void recordEnvironmentInfo() {

        try {

            IHookBridge hookBridge = ModuleLoader.getHookBridge();

            StringBuilder header = buildHeaderInfo();
            StringBuilder deviceInfo = buildDeviceInfo();
            StringBuilder xposedInfo = buildXposedInfo(hookBridge.getFrameworkName(), hookBridge.getFrameworkVersion(),
                    hookBridge.getFrameworkVersionCode(), hookBridge.getApiLevel());
            StringBuilder hostAppInfo = buildHostAppInfo(HostInfo.getPackageName(), HostInfo.getVersionName(), HostInfo.getVersionCode());
            StringBuilder moduleInfo = buildModuleInfo();
            StringBuilder footer = buildFooterInfo();

            String completeInfo = String.valueOf(header) +
                    deviceInfo +
                    xposedInfo +
                    hostAppInfo +
                    moduleInfo +
                    footer;

            FileWriter writer = new FileWriter(DataUtils.createFile("log", "environment_info.txt"), false);
            writer.write(completeInfo);
            writer.close();

        } catch (Exception e) {
            // 忽略文件写入异常
        }
    }
}