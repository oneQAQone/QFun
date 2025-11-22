package me.yxp.qfun.utils.error;

import android.util.Log;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.utils.qq.ToastUtils;

public class PluginError {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void evalError(Throwable throwable, PluginInfo pluginInfo) {
        logPluginError("evalError", throwable, pluginInfo, null);
    }

    public static void callError(Throwable throwable, PluginInfo pluginInfo) {
        logPluginError("callError", throwable, pluginInfo, null);
    }

    public static void findError(Throwable throwable, PluginInfo pluginInfo, String callback) {
        logPluginError("findError", throwable, pluginInfo, "回调(" + callback + ")未找到");
    }

    public static void log(PluginInfo pluginInfo, String fileName, String text) {
        try {

            // 写入错误文件
            FileWriter writer = new FileWriter(pluginInfo.pluginPath + "/" + fileName, true);
            writer.write(text);
            writer.close();

        } catch (Exception e) {
            // 忽略文件写入异常
        }
    }

    private static void logPluginError(String errorType, Throwable throwable,
                                       PluginInfo pluginInfo, String additionalInfo) {

            String time = DATE_FORMAT.format(new Date());
            String stackTrace = Log.getStackTraceString(throwable);

            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("\n\n\n").append(errorType).append("\n\n");
            logBuilder.append("Time:").append(time).append("\n\n");

            if (additionalInfo != null) {
                logBuilder.append(additionalInfo).append("\n\n");
            }

            logBuilder.append(stackTrace);

            String logContent = logBuilder.toString();

            // 显示Toast提示
            ToastUtils.Toast(throwable.toString());

            // 写入错误文件
            log(pluginInfo, "error.txt", logContent);
    }
}
