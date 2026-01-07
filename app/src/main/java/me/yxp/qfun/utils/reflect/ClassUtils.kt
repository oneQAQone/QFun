package me.yxp.qfun.utils.reflect


import com.tencent.mobileqq.pluginsdk.PluginStatic
import me.yxp.qfun.utils.qq.HostInfo

object ClassUtils {
    private var _hostClassLoader: ClassLoader? = null

    var hostClassLoader: ClassLoader
        get() = _hostClassLoader ?: throw IllegalStateException("HostClassLoader not initialized")
        set(value) {
            _hostClassLoader = value
        }

    val moduleClassLoader: ClassLoader by lazy { this::class.java.classLoader!! }

    fun loadClassOrNull(className: String): Class<*>? {
        return runCatching { hostClassLoader.loadClass(className) }.getOrNull()
    }

    fun loadClass(className: String): Class<*> {
        return hostClassLoader.loadClass(className)
    }

    fun loadFromPlugin(pluginName: String, className: String): Class<*> {

        val pluginClassLoader =
            PluginStatic.getOrCreateClassLoader(HostInfo.hostContext, pluginName)
        return pluginClassLoader.loadClass(className)

    }
}

val String.clazz: Class<*>?
    get() = ClassUtils.loadClassOrNull(this)

val String.toClass: Class<*>
    get() = ClassUtils.loadClass(this)
