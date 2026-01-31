package me.yxp.qfun.lifecycle

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.os.PersistableBundle
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.R
import me.yxp.qfun.activity.BaseComposeActivity
import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.callStaticMethod
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObject
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import me.yxp.qfun.utils.reflect.getStaticObject
import me.yxp.qfun.utils.reflect.setObject
import me.yxp.qfun.utils.reflect.toClass
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
@Suppress("DEPRECATION")
object Parasitics {
    private const val STUB_DEFAULT_ACTIVITY =
        "com.tencent.mobileqq.activity.photo.CameraPreviewActivity"
    private const val ACTIVITY_PROXY_INTENT = "ACTIVITY_PROXY_INTENT"

    private val moduleLoader by lazy { ClassUtils.moduleClassLoader }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    private object ResourcesLoaderHolderApi30 {
        var sResourcesLoader: ResourcesLoader? = null
    }

    private fun isTargetActivity(className: String?): Boolean {
        if (className == null) return false
        if (DynamicActivityRegistry.contains(className)) return true
        if (!className.startsWith(BuildConfig.APPLICATION_ID)) return false

        return runCatching {
            val targetClass = moduleLoader.loadClass(className)
            BaseComposeActivity::class.java.isAssignableFrom(targetClass)
        }.getOrElse { false }
    }

    fun initForStubActivity(ctx: Context) {
        runCatching {
            val activityThreadClass = "android.app.ActivityThread".toClass
            val currentActivityThread =
                activityThreadClass.callStaticMethod("currentActivityThread") ?: return

            // Hook Instrumentation (负责实例化 Activity)
            val instrumentation =
                currentActivityThread.getObject("mInstrumentation") as Instrumentation
            if (instrumentation !is ProxyInstrumentation) {
                currentActivityThread.setObject(
                    "mInstrumentation",
                    ProxyInstrumentation(instrumentation)
                )
            }

            // Hook H (主线程 Handler，负责消息分发)
            val mH = currentActivityThread.getObject("mH") as Handler
            val originalCallback = mH.getObjectByTypeOrNull<Handler.Callback>(Handler::class.java)

            mH.setObject("mCallback", Handler.Callback { msg ->
                val handledObj = when (msg.what) {
                    100 -> msg.obj // LAUNCH_ACTIVITY
                    159 -> msg.obj // EXECUTE_TRANSACTION
                    else -> null
                }
                handledObj?.let { handleLaunchMessage(it, msg.what) }
                originalCallback?.handleMessage(msg) ?: false
            }, Handler::class.java)

            hookIActivityManager()
            hookIPackageManager(ctx, currentActivityThread)
        }
    }

    private fun hookIActivityManager() {
        val singletonInstance = resolveActivityManagerSingleton() ?: return
        val mInstance = singletonInstance.getObject("mInstance", "android.util.Singleton".toClass)

        val iamInterface = if (Build.VERSION.SDK_INT >= 29) {
            "android.app.IActivityTaskManager".toClass
        } else {
            "android.app.IActivityManager".toClass
        }

        val proxy = Proxy.newProxyInstance(moduleLoader, arrayOf(iamInterface)) { _, method, args ->
            // 拦截 startActivity
            if ("startActivity" == method.name) {
                args?.indexOfFirst { it is Intent }?.takeIf { it != -1 }?.let { index ->
                    val raw = args[index] as Intent
                    val component = raw.component
                    if (component != null && HostInfo.packageName == component.packageName &&
                        isTargetActivity(component.className)
                    ) {

                        args[index] = Intent().apply {
                            setClassName(component.packageName, STUB_DEFAULT_ACTIVITY)
                            putExtra(ACTIVITY_PROXY_INTENT, raw)
                            flags = raw.flags
                        }
                    }
                }
            }
            invokeOriginal(mInstance, method, args)
        }

        singletonInstance.setObject("mInstance", proxy, "android.util.Singleton".toClass)
    }

    private fun hookIPackageManager(ctx: Context, currentActivityThread: Any) {
        val sPackageManager = currentActivityThread.getObject("sPackageManager")
        val ipmInterface = "android.content.pm.IPackageManager".toClass

        val proxy = Proxy.newProxyInstance(
            ipmInterface.classLoader,
            arrayOf(ipmInterface)
        ) { _, method, args ->
            // 拦截 getActivityInfo，伪造信息防止 PMS 报错
            if ("getActivityInfo" == method.name && args?.isNotEmpty() == true) {
                val component = args[0] as? ComponentName
                if (component != null && HostInfo.packageName == component.packageName &&
                    isTargetActivity(component.className)
                ) {
                    val flags = (args[1] as? Number)?.toLong() ?: 0L
                    return@newProxyInstance CounterfeitActivityInfoFactory.makeProxyActivityInfo(
                        component.className,
                        flags
                    )
                }
            }
            invokeOriginal(sPackageManager, method, args)
        }

        currentActivityThread.setObject("sPackageManager", proxy)
        ctx.packageManager.setObject("mPM", proxy)
    }

    // 统一处理反射调用异常
    private fun invokeOriginal(target: Any, method: Method, args: Array<Any?>?): Any? {
        return try {
            method.invoke(target, *args.orEmpty())
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }

    private fun resolveActivityManagerSingleton(): Any? {
        // 依次尝试不同 Android 版本的 AMS 单例位置 (静态字段)
        return runCatching {
            // Android 10+
            val atmClass = "android.app.ActivityTaskManager".toClass
            val singleton = atmClass.getStaticObject("IActivityTaskManagerSingleton")
            "android.util.Singleton".toClass.findMethod { name = "get" }.invoke(singleton)
            singleton
        }.recoverCatching {
            // Android 8.0 - 9.0
            "android.app.ActivityManager".toClass.getStaticObject("IActivityManagerSingleton")
        }.recoverCatching {
            // Android 8.0 以下
            "android.app.ActivityManagerNative".toClass.getStaticObject("gDefault")
        }.getOrNull()
    }

    // ================= Intent 还原逻辑 =================

    private fun handleLaunchMessage(obj: Any, what: Int) {
        if (what == 100) {
            // ActivityClientRecord
            val intent = obj.getObject("intent") as? Intent ?: return
            unwrapIntent(intent)?.let { obj.setObject("intent", it) }
        } else if (what == 159) {
            // ClientTransaction
            val callbacks =
                runCatching { obj.callMethod("getCallbacks") as? List<*> }.getOrNull() ?: return
            callbacks.forEach { item ->
                if (item != null && item.javaClass.name.contains("LaunchActivityItem")) {
                    val intent = item.getObject("mIntent") as? Intent ?: return
                    val original = unwrapIntent(intent) ?: return

                    item.setObject("mIntent", original)

                    // Android 12+ 修复 ActivityThread 内部缓存
                    if (Build.VERSION.SDK_INT >= 31) {
                        fixActivityClientRecordForApi31(obj, original)
                    }
                }
            }
        }
    }

    private fun fixActivityClientRecordForApi31(transaction: Any, originalIntent: Intent) {
        runCatching {
            val token = transaction.callMethod("getActivityToken") as? IBinder ?: return
            val activityThread =
                "android.app.ActivityThread".toClass.callStaticMethod("currentActivityThread")
            val acr = activityThread?.callMethod("getLaunchingActivity", token) ?: return
            acr.setObject("intent", originalIntent)
        }
    }

    private fun unwrapIntent(intent: Intent): Intent? {
        return runCatching {
            val clone = intent.clone() as Intent
            clone.extras?.classLoader = ClassUtils.hostClassLoader
            if (clone.hasExtra(ACTIVITY_PROXY_INTENT)) {
                clone.getParcelableExtra<Intent>(ACTIVITY_PROXY_INTENT)?.apply {
                    extras?.classLoader = moduleLoader
                }
            } else {
                null
            }
        }.getOrNull()
    }

    // ================= 资源注入逻辑 =================

    fun injectModuleResources(res: Resources?) {
        if (res == null || runCatching { res.getString(R.string.app_name) }.isSuccess) return
        val path = ModuleLoader.getMODULE_PATH()

        if (Build.VERSION.SDK_INT >= 30) {
            val loader = ResourcesLoaderHolderApi30.sResourcesLoader ?: run {
                runCatching {
                    val pfd =
                        ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
                    val provider = ResourcesProvider.loadFromApk(pfd)
                    ResourcesLoader().apply { addProvider(provider) }
                }.getOrNull()?.also { ResourcesLoaderHolderApi30.sResourcesLoader = it }
            }

            val task = Runnable {
                try {
                    loader?.let { res.addLoaders(it) }
                } catch (e: IllegalArgumentException) {
                    if (e.message?.contains("Cannot modify resource loaders") == true) injectResourcesBelowApi30(
                        res,
                        path
                    )
                }
            }
            if (Looper.myLooper() == Looper.getMainLooper()) task.run() else mainHandler.post(task)
        } else {
            injectResourcesBelowApi30(res, path)
        }
    }

    private fun injectResourcesBelowApi30(res: Resources, path: String) {
        runCatching {
            AssetManager::class.java.findMethod {
                name = "addAssetPath"; paramTypes(String::class.java)
            }
                .invoke(res.assets, path)
        }
    }

    // ================= Instrumentation 代理类 =================

    private class ProxyInstrumentation(private val mBase: Instrumentation) : Instrumentation() {
        override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
            if (DynamicActivityRegistry.contains(className)) {
                DynamicActivityRegistry.getActivityClass(className)?.let { return it.newInstance() }
            }
            return try {
                mBase.newActivity(cl, className, intent)
            } catch (_: Exception) {
                // 宿主加载失败，切换到模块 ClassLoader
                Parasitics::class.java.classLoader!!.loadClass(className).newInstance() as Activity
            }
        }

        override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
            injectModuleResources(activity.resources)
            if (icicle != null && isTargetActivity(activity.javaClass.name)) icicle.classLoader =
                moduleLoader
            mBase.callActivityOnCreate(activity, icicle)
        }

        override fun callActivityOnCreate(
            activity: Activity,
            icicle: Bundle?,
            persistentState: PersistableBundle?
        ) {
            injectModuleResources(activity.resources)
            if (icicle != null && isTargetActivity(activity.javaClass.name)) icicle.classLoader =
                moduleLoader
            mBase.callActivityOnCreate(activity, icicle, persistentState)
        }
    }
}