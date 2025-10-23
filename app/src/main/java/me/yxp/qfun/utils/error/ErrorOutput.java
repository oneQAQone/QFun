package me.yxp.qfun.utils.error;

import android.util.Log;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;

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
            FileWriter writer = new FileWriter(DataUtils.createFile("log", date + ".txt"), true);
            writer.write(errorMessage);
            writer.close();
        } catch (Exception e) {
            // 忽略文件写入异常
        }
    }
}