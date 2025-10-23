package me.yxp.qfun.loader.xp51;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.yxp.qfun.BuildConfig;
import me.yxp.qfun.common.ModuleLoader;
import me.yxp.qfun.utils.qq.HostInfo;

/**
 * Entry point for started Xposed API 51-99.
 * <p>
 * Xposed is used as ART hook implementation.
 */
@Keep
public class Xp51HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String PACKAGE_NAME_QQ = HostInfo.PACKAGE_NAME_QQ;

    private static final String PACKAGE_NAME_TIM = HostInfo.PACKAGE_NAME_TIM;
    private static final String PACKAGE_NAME_SELF = BuildConfig.APPLICATION_ID;
    public static String sCurrentPackageName = null;
    private static XC_LoadPackage.LoadPackageParam sLoadPackageParam = null;
    private static IXposedHookZygoteInit.StartupParam sInitZygoteStartupParam = null;
    private static String sModulePath = null;

    /**
     * Get the {@link XC_LoadPackage.LoadPackageParam} of the current module.
     *
     * @return the lpparam
     */
    public static XC_LoadPackage.LoadPackageParam getLoadPackageParam() {
        if (sLoadPackageParam == null) {
            throw new IllegalStateException("LoadPackageParam is null");
        }
        return sLoadPackageParam;
    }

    /**
     * Get the path of the current module.
     *
     * @return the module path
     */
    public static String getModulePath() {
        if (sModulePath == null) {
            throw new IllegalStateException("Module path is null");
        }
        return sModulePath;
    }

    /**
     * Get the {@link IXposedHookZygoteInit.StartupParam} of the current module.
     *
     * @return the initZygote param
     */
    public static IXposedHookZygoteInit.StartupParam getInitZygoteStartupParam() {
        if (sInitZygoteStartupParam == null) {
            throw new IllegalStateException("InitZygoteStartupParam is null");
        }
        return sInitZygoteStartupParam;
    }

    /**
     * *** No kotlin code should be invoked here.*** May cause a crash.
     */
    @Keep
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws ReflectiveOperationException {
        sLoadPackageParam = lpparam;
        // check LSPosed dex-obfuscation
        Class<?> kXposedBridge = XposedBridge.class;
        switch (lpparam.packageName) {
            case PACKAGE_NAME_SELF: {
                Xp51HookStatusInit.init(lpparam.classLoader);
                break;
            }

            case PACKAGE_NAME_TIM:
            case PACKAGE_NAME_QQ: {
                if (sInitZygoteStartupParam == null) {
                    throw new IllegalStateException("handleLoadPackage: sInitZygoteStartupParam is null");
                }
                sCurrentPackageName = lpparam.packageName;
                ModuleLoader.initialize(lpparam.classLoader, Xp51HookImpl.INSTANCE, Xp51HookImpl.INSTANCE, getModulePath());
                break;
            }

            default:
                break;
        }
    }

    /**
     * *** No kotlin code should be invoked here.*** May cause a crash.
     */
    @Override
    public void initZygote(StartupParam startupParam) {
        sInitZygoteStartupParam = startupParam;
        sModulePath = startupParam.modulePath;
    }

}
