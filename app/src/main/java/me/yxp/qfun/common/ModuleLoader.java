package me.yxp.qfun.common;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

import me.yxp.qfun.loader.hookapi.HookEngineManager;
import me.yxp.qfun.utils.qq.HostInfo;

public class ModuleLoader {
    private static boolean sLoaded = false;
    private static String MODULE_PATH;

    public static void initialize(
            @NonNull ClassLoader hostClassLoader,
            @NonNull String selfPath,
            @NonNull String packageName,
            @NonNull String processName
    ) {
        if (sLoaded) {
            return;
        }

        ModuleLoader.MODULE_PATH = selfPath;

        if (packageName.equals(processName)) {
            HookEngineManager.engine.log(Log.INFO,"[QFun]", "ModuleLoader init", null);
        }

        HostInfo.INSTANCE.setPackageName(packageName);
        HostInfo.INSTANCE.setProcessName(processName);

        Startup.init(hostClassLoader);

        sLoaded = true;

    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("DiscouragedPrivateApi")
    public static void injectClassLoader(ClassLoader hostClassLoader) {
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

    public static String getMODULE_PATH() {
        return MODULE_PATH;
    }

}
