package me.yxp.qfun.loader.zygisk

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.annotation.Keep
import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.loader.hookapi.HookEngineManager
import me.yxp.qfun.loader.hookapi.Unhook
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.getObjectOrNull

@Keep
object ZygiskHookEntry {

    @Volatile
    private var unhookHandle: Unhook? = null

    @JvmStatic
    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun init(processName: String, apkPath: String) {
        if (HookEngineManager.isInitialized) return
        HookEngineManager.engine = ZygiskHookEngine()

        val targetPackage = processName.substringBefore(':')
        val loadedApkClass = Class.forName("android.app.LoadedApk")
        val getClassLoaderMethod = loadedApkClass.getDeclaredMethod("getClassLoader")

        unhookHandle = getClassLoaderMethod.hookAfter { param ->
            val hostLoader = param.result as? ClassLoader ?: return@hookAfter
            val loadedApk = param.thisObject

            val pkgName = loadedApk.getObjectOrNull("mPackageName") as? String
            if (pkgName != targetPackage) return@hookAfter

            val appInfo = loadedApk.getObjectOrNull("mApplicationInfo") as? ApplicationInfo
            if (appInfo == null || appInfo.packageName != targetPackage) return@hookAfter

            val sourceDir = appInfo.sourceDir ?: ""
            if (!sourceDir.contains(targetPackage)) return@hookAfter

            unhookHandle?.unhook()
            unhookHandle = null

            ModuleLoader.initialize(hostLoader, apkPath, targetPackage, processName)
        }
    }
}