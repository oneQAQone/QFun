package me.yxp.qfun.loader.legacy

import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.loader.hookapi.HookEngineManager
import me.yxp.qfun.utils.qq.HostInfo

@Keep
class LegacyHookEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private var modulePath: String = ""

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName

        if (packageName == BuildConfig.APPLICATION_ID) {
            LegacyHookStatusInit.init(lpparam.classLoader)
        }

        if (packageName == HostInfo.PACKAGE_NAME_QQ || packageName == HostInfo.PACKAGE_NAME_TIM) {
            
            if (HookEngineManager.isInitialized) return

            HookEngineManager.engine = LegacyHookEngine()
            
            ModuleLoader.initialize(
                lpparam.classLoader,
                modulePath,
                packageName,
                lpparam.processName
            )
        }
    }
}