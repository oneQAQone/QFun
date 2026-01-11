package me.yxp.qfun.common

import android.content.Context
import com.tencent.common.app.BaseApplicationImpl
import dalvik.system.BaseDexClassLoader
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.lifecycle.Parasitics
import me.yxp.qfun.utils.dexkit.DexKitCache
import me.yxp.qfun.utils.dexkit.DexKitFinder
import me.yxp.qfun.utils.hook.hook
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import java.util.concurrent.atomic.AtomicBoolean

object Startup {

    private var isInit = AtomicBoolean(false)
    private var hasCapturedTinker = AtomicBoolean(false)

    @JvmStatic
    fun init(initialLoader: ClassLoader) {

        runCatching {
            initialLoader.loadClass("com.tencent.common.app.QFixApplicationImplProxy")
        }.getOrNull()
            ?.let {
                hookQFixAttach(it)
            }
            ?: doRealStartup(initialLoader)

    }

    private fun hookQFixAttach(qfixClass: Class<*>) {
        val constructorUnhooks = ArrayList<XC_MethodHook.Unhook>()

        qfixClass.getDeclaredMethod("attachBaseContext", Context::class.java).hook {
            before {

                BaseDexClassLoader::class.java.declaredConstructors.forEach { ctor ->
                    val unhook = ctor.hook {
                        after { param ->
                            val loader = param.thisObject as ClassLoader
                            val loaderStr = loader.toString()

                            if (loaderStr.contains(BuildConfig.APPLICATION_ID)) return@after

                            if ((loaderStr.contains("com.tencent.") ||
                                        loaderStr.contains("TinkerClassLoader") ||
                                        loaderStr.contains("DelegateLastClassLoader")) &&
                                !hasCapturedTinker.get()
                            ) {
                                hasCapturedTinker.set(true)
                                XposedBridge.log("[QFun] 捕获到热更 ClassLoader: $loader")
                                doRealStartup(loader)
                            }
                        }
                    }
                    constructorUnhooks.add(unhook)
                }
            }

            after { param ->

                constructorUnhooks.forEach { it.unhook() }
                constructorUnhooks.clear()

                if (!hasCapturedTinker.get()) {
                    val context = param.args[0] as Context
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

            BaseApplicationImpl::class.java
                .getDeclaredMethod("onCreate")
                .hookAfter { param ->

                    if (isInit.compareAndSet(false, true)) {
                        val hostContext = param.thisObject as Context

                        HostInfo.init(hostContext)

                        if (HostInfo.processName == HostInfo.packageName) {
                            XposedBridge.log("[QFun] 宿主启动 (Loader: $realClassLoader)")
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
            XposedBridge.log(th)
        }
    }

}