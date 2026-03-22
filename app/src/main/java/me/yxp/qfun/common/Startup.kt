package me.yxp.qfun.common

import android.content.Context
import android.util.Log
import com.tencent.common.app.BaseApplicationImpl
import dalvik.system.BaseDexClassLoader
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.lifecycle.Parasitics
import me.yxp.qfun.loader.hookapi.HookEngineManager
import me.yxp.qfun.loader.hookapi.Unhook
import me.yxp.qfun.utils.dexkit.DexKitCache
import me.yxp.qfun.utils.dexkit.DexKitFinder
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicBoolean

object Startup {
    private var isInit = AtomicBoolean(false)
    private var hasCapturedTinker = AtomicBoolean(false)

    @JvmStatic
    fun init(initialLoader: ClassLoader) {
        runCatching {
            initialLoader.loadClass("com.tencent.common.app.QFixApplicationImplProxy")
                .getDeclaredMethod("attachBaseContext", Context::class.java)
        }.getOrNull()
            ?.let { hookQFixAttach(it) }
            ?: doRealStartup(initialLoader)
    }

    private fun hookQFixAttach(attach: Method) {

        attach.hookReplace { chain ->
            val constructorUnhooks = mutableListOf<Unhook>()

            BaseDexClassLoader::class.java.declaredConstructors.forEach { ctor ->
                val unhook = ctor.hookAfter { param ->
                    val loader = param.thisObject as ClassLoader
                    val loaderStr = loader.toString()
                    if (loaderStr.contains(BuildConfig.APPLICATION_ID)) return@hookAfter

                    if ((loaderStr.contains("com.tencent.") ||
                                loaderStr.contains("TinkerClassLoader") ||
                                loaderStr.contains("DelegateLastClassLoader"))
                        && !hasCapturedTinker.get()
                    ) {
                        hasCapturedTinker.set(true)

                        HookEngineManager.engine.log(
                            Log.INFO,
                            "[QFun]",
                            "捕获到热更 ClassLoader: $loader"
                        )
                        doRealStartup(loader)
                    }
                }
                constructorUnhooks.add(unhook)
            }


            try {
                return@hookReplace chain.proceed()
            } finally {

                constructorUnhooks.forEach { it.unhook() }
                constructorUnhooks.clear()

                if (!hasCapturedTinker.get()) {
                    val context = chain.args[0] as Context
                    doRealStartup(context.classLoader)
                }
            }
        }
    }

    @Synchronized
    private fun doRealStartup(realClassLoader: ClassLoader) {
        if (isInit.get()) return
        ClassUtils.hostClassLoader = realClassLoader
        ModuleLoader.injectClassLoader(realClassLoader)
        CrashMonitor.init()

        try {
            BaseApplicationImpl::class.java.getDeclaredMethod("onCreate").hookAfter { param ->
                if (isInit.compareAndSet(false, true)) {
                    val hostContext = param.thisObject as Context
                    HostInfo.init(hostContext)

                    if (HostInfo.processName == HostInfo.packageName) {

                        HookEngineManager.engine.log(
                            Log.INFO,
                            "[QFun]",
                            "宿主启动 (Loader: $realClassLoader)"
                        )
                        LogUtils.logEnvironment()
                    }

                    Parasitics.initForStubActivity(hostContext)
                    Parasitics.injectModuleResources(hostContext.resources)

                    if (DexKitCache.initCache()) {
                        MainHook.loadHook()
                    } else {
                        DexKitFinder.doFind()
                    }
                }
            }
        } catch (th: Throwable) {
            HookEngineManager.engine.log(Log.ERROR, "[QFun]", "doRealStartup 发生异常", th)
        }
    }
}