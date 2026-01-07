package me.yxp.qfun.common

import android.content.Context
import com.tencent.common.app.BaseApplicationImpl
import me.yxp.qfun.hook.MainHook
import me.yxp.qfun.lifecycle.Parasitics
import me.yxp.qfun.utils.dexkit.DexKitCache
import me.yxp.qfun.utils.dexkit.DexKitFinder
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo

object Startup {

    private var isFirst = true

    @JvmStatic
    fun hookApplicationCreate() {

        BaseApplicationImpl::class.java
            .getDeclaredMethod("onCreate")
            .hookAfter {

                if (!isFirst) return@hookAfter
                isFirst = false

                val hostContext = it.thisObject as Context

                HostInfo.init(hostContext)
                if (HostInfo.processName == HostInfo.packageName) {
                    XposedBridge.log("[QFnu] attachHostContext:${HostInfo.packageName}")
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
}