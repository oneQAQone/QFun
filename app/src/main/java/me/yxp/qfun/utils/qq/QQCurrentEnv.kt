package me.yxp.qfun.utils.qq

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.tencent.mobileqq.app.BusinessHandler
import com.tencent.mobileqq.app.QQAppInterface
import com.tencent.mobileqq.qroute.QRoute
import com.tencent.mobileqq.qroute.QRouteApi
import com.tencent.qqnt.kernel.api.IKernelService
import com.tencent.qqnt.kernel.api.impl.KernelServiceImpl
import me.yxp.qfun.utils.reflect.callStaticMethod
import me.yxp.qfun.utils.reflect.getObject
import me.yxp.qfun.utils.reflect.toClass
import mqq.app.MobileQQ
import mqq.app.api.IRuntimeService

@Suppress("DEPRECATION")
object QQCurrentEnv {

    val globalPreference: SharedPreferences by lazy {
        HostInfo.hostContext.getSharedPreferences(
            "QFun_Config_global",
            Context.MODE_MULTI_PROCESS
        )
    }

    val qQAppInterface
        get() = MobileQQ.getMobileQQ().peekAppRuntime() as QQAppInterface

    val activity: Activity?
        get() = runCatching {

            val activityThreadClass = "android.app.ActivityThread".toClass
            val activityThread = activityThreadClass.callStaticMethod("currentActivityThread")
            val activities =
                activityThread?.getObject("mActivities") as? Map<*, *>
                    ?: return@runCatching null

            activities.values.firstNotNullOfOrNull { record ->
                record ?: return@firstNotNullOfOrNull null

                val paused = record.getObject("paused") as Boolean
                if (!paused) {
                    record.getObject("activity") as Activity
                } else {
                    null
                }
            }
        }.getOrNull()


    val currentDir: String
        get() = "${HostInfo.moduleDataPath}$currentUin/"

    val currentUin: String
        get() = runCatching {
            qQAppInterface.currentUin
        }.getOrElse {
            globalPreference.getString("currentUin", "global")!!
        }

    val currentNickName: String?
        get() = qQAppInterface.currentNickname

    val kernelMsgService
        get() = runCatching {

            val kernelService = runtime<IKernelService>() as KernelServiceImpl
            kernelService.msgService.service

        }.getOrNull()

}


internal inline fun <reified T : QRouteApi> api(): T {
    return QRoute.api(T::class.java)
}

internal inline fun <reified T : IRuntimeService> runtime(): T {
    return QQCurrentEnv.qQAppInterface.getRuntimeService(T::class.java, "")
}

internal inline fun <reified T : BusinessHandler> handler(): BusinessHandler {
    return QQCurrentEnv.qQAppInterface.getBusinessHandler(T::class.java.name)
}