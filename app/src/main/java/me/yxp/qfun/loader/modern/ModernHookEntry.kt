package me.yxp.qfun.loader.modern

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.annotation.Keep
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterfaceWrapper
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.loader.hookapi.HookEngineManager
import me.yxp.qfun.loader.legacy.LegacyHookEngine
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.getObject
import me.yxp.qfun.utils.reflect.setObject

@Keep
class ModernHookEntry : XposedModule {

    private var processName = ""
    private var isApi100Fallback = false

    /* --- start of API 100 --- */
    constructor(base: XposedInterface, param: XposedModuleInterface.ModuleLoadedParam) {
        // 相当于调用 super(base, param)
        this.setObject("mBase", base, XposedInterfaceWrapper::class.java)
        processName = param.processName
        // 降级为 Legacy API
        isApi100Fallback = true
        
        if (HookEngineManager.isInitialized) return
        HookEngineManager.engine = LegacyHookEngine()
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (isApi100Fallback) {
            val packageName = param.packageName
            val base = this.getObject("mBase", XposedInterfaceWrapper::class.java)
            val applicationInfo = base.callMethod("getApplicationInfo") as ApplicationInfo

            if (packageName == HostInfo.PACKAGE_NAME_QQ || packageName == HostInfo.PACKAGE_NAME_TIM) {
                if (param.isFirstPackage) {
                    ModuleLoader.initialize(
                        param.callMethod("getClassLoader") as ClassLoader,
                        applicationInfo.sourceDir,
                        packageName,
                        processName
                    )

                    HookEngineManager.engine.log(
                        Log.INFO,
                        "[QFun]",
                        "在 API 100 中加载，已降级为 Legacy API"
                    )
                }
            }
        }
    }
    /* --- end of API 100 --- */


    /* --- start of API 101 --- */
    constructor() : super()

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        super.onModuleLoaded(param)
        this.processName = param.processName
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        if (HookEngineManager.isInitialized) return

        val packageName = param.packageName

        if (packageName == HostInfo.PACKAGE_NAME_QQ || packageName == HostInfo.PACKAGE_NAME_TIM) {
            if (param.isFirstPackage) {
                HookEngineManager.engine = ModernHookEngine(this)
                
                ModuleLoader.initialize(
                    param.classLoader,
                    moduleApplicationInfo.sourceDir,
                    packageName,
                    processName
                )
            }
        }
    }
    /* --- end of API 101 --- */
}