package me.yxp.qfun.utils.dexkit

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.yxp.qfun.BuildConfig
import me.yxp.qfun.utils.io.ObjectStore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexMethod
import java.io.File
import java.lang.reflect.Method

object DexKitCache {

    var cacheMap = mutableMapOf<String, String>()

    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    val cacheFile by lazy {
        File(
            "${HostInfo.moduleDataPath}/global/dexkit",
            "CacheMap_${HostInfo.versionCode}_${BuildConfig.VERSION_CODE}"
        )
    }

    fun initCache(): Boolean {
        cacheMap = ObjectStore.load(cacheFile, mapSerializer)?.toMutableMap() ?: return false
        return true
    }

    fun saveCache() = ObjectStore.save(cacheFile, cacheMap.toMap(), mapSerializer)

    fun getClass(key: String): Class<*> =
        cacheMap[key]?.let {
            DexClass(it).getInstance(ClassUtils.hostClassLoader)
        } ?: throw ClassNotFoundException(key)

    fun getMethod(key: String): Method =
        cacheMap[key]?.let {
            DexMethod(it).getMethodInstance(ClassUtils.hostClassLoader)
        } ?: throw NoSuchMethodException(key)
}
