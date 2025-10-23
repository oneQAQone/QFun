package me.yxp.qfun.loader.lsp100;

import android.content.pm.ApplicationInfo;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import me.yxp.qfun.common.ModuleLoader;
import me.yxp.qfun.utils.qq.HostInfo;

/**
 * Entry point for libxpsoed API 100 (typically LSPosed).
 * <p>
 * The libxpsoed API is used as ART hook implementation.
 */
@Keep
public class Lsp100HookEntry extends XposedModule {

    private static final String PACKAGE_NAME_QQ = HostInfo.PACKAGE_NAME_QQ;

    private static final String PACKAGE_NAME_TIM = HostInfo.PACKAGE_NAME_TIM;

    public static PackageLoadedParam packageLoadedParam;

    /**
     * Instantiates a new Xposed module.
     * <p>
     * When the module is loaded into the target process, the constructor will be called.
     *
     * @param base  The implementation interface provided by the framework, should not be used by the module
     * @param param Information about the process in which the module is loaded
     */
    public Lsp100HookEntry(@NonNull XposedInterface base, @NonNull ModuleLoadedParam param) {
        super(base, param);
        Lsp100HookImpl.init(this);
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam param) {
        String packageName = param.getPackageName();
        packageLoadedParam = param;
        // Do nothing
        if (packageName.equals(PACKAGE_NAME_QQ) || packageName.equals(PACKAGE_NAME_TIM)) {// Initialize the module
            if (param.isFirstPackage()) {
                String modulePath = this.getApplicationInfo().sourceDir;
                handleLoadHostPackage(param.getClassLoader(), param.getApplicationInfo(), modulePath);
            }
        }
    }

    private void handleLoadHostPackage(@NonNull ClassLoader cl, @NonNull ApplicationInfo ai, @NonNull String modulePath) {

        try {
            ModuleLoader.initialize(cl, Lsp100HookImpl.INSTANCE, Lsp100HookImpl.INSTANCE, modulePath);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
