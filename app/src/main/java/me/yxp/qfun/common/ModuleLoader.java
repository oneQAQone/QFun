package me.yxp.qfun.common;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.yxp.qfun.loader.hookapi.IHookBridge;
import me.yxp.qfun.loader.hookapi.ILoaderService;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;

public class ModuleLoader {

    private static final ArrayList<Throwable> sInitErrors = new ArrayList<>(1);

    private static boolean sLoaded = false;

    private static IHookBridge hookBridge;

    private static ILoaderService loaderService;

    private static String MODULE_PATH;

    public static void initialize(
            @NonNull ClassLoader hostClassLoader,
            @NonNull ILoaderService loaderService,
            @Nullable IHookBridge hookBridge,
            @NonNull String selfPath,
            @NonNull String packageName,
            @NonNull String processName
    ) throws ReflectiveOperationException {
        if (sLoaded) {
            return;
        }
        ModuleLoader.hookBridge = hookBridge;
        ModuleLoader.loaderService = loaderService;
        ModuleLoader.MODULE_PATH = selfPath;

        if (packageName.equals(processName)) {
            XposedBridge.log("[QFun] ModuleLoader init");
        }

        CrashMonitor.init();

        HostInfo.INSTANCE.setPackageName(packageName);
        HostInfo.INSTANCE.setProcessName(processName);
        ClassUtils.INSTANCE.setHostClassLoader(hostClassLoader);
        injectClassLoader(hostClassLoader);

        Startup.hookApplicationCreate();

        sLoaded = true;

    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("DiscouragedPrivateApi")
    private static void injectClassLoader(ClassLoader hostClassLoader) {
        HybridClassLoader.setHostClassLoader(hostClassLoader);
        HybridClassLoader loader = HybridClassLoader.INSTANCE;
        ClassLoader self = ModuleLoader.class.getClassLoader();
        assert self != null;
        ClassLoader parent = self.getParent();
        HybridClassLoader.setLoaderParentClassLoader(parent);
        try {
            Field fParent = ClassLoader.class.getDeclaredField("parent");
            fParent.setAccessible(true);
            fParent.set(self, loader);
        } catch (Exception ignored) {
        }
    }

    public static List<Throwable> getInitErrors() {
        return sInitErrors;
    }

    public static IHookBridge getHookBridge() {
        return hookBridge;
    }

    public static ILoaderService getLoaderService() {
        return loaderService;
    }

    public static String getMODULE_PATH() {
        return MODULE_PATH;
    }

}
