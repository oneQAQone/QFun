package me.yxp.qfun.utils.thread;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.HostInfo;

public class SyncUtils {
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static Handler sHandler;

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        sHandler.postDelayed(runnable, delayMillis);
    }

    public static void post(Runnable runnable) {
        postDelayed(runnable, 0L);
    }

    public static void async(Runnable runnable) {
        sExecutor.execute(runnable);
    }

    public static void runOnNewThread(String prefix, MyRunnable myRunnable) {
        new Thread(() -> {
            try {
                myRunnable.run();
            } catch (Throwable th) {
                ErrorOutput.Error(prefix, th);
            }
        }).start();
    }

    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        Context context = HostInfo.getHostContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (am == null) {
            return null;
        }

        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    public interface MyRunnable {
        void run() throws Throwable;
    }
}
