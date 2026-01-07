package me.yxp.qfun.plugin.loader

import me.yxp.qfun.common.ModuleLoader
import me.yxp.qfun.utils.reflect.ClassUtils
import java.net.URL
import java.util.Collections
import java.util.Enumeration

class FixClassLoader : ClassLoader(getSystemClassLoader()) {
    private val loaders = ArrayList<ClassLoader>()

    init {
        loaders.add(getSystemClassLoader())
        loaders.add(ClassUtils.moduleClassLoader)
        loaders.add(ClassUtils.hostClassLoader)
        try {
            loaders.add(ModuleLoader::class.java.classLoader!!)
        } catch (_: Throwable) {
        }
    }

    override fun findClass(name: String): Class<*>? {
        for (loader in loaders) {
            try {
                return loader.loadClass(name)
            } catch (_: Exception) {
            }
        }
        return null
    }

    override fun getResource(name: String): URL? {
        for (loader in loaders) {
            val resource = loader.getResource(name)
            if (resource != null) {
                return resource
            }
        }
        return super.getResource(name)
    }

    override fun getResources(name: String): Enumeration<URL> {
        val urlList = ArrayList<URL>()
        for (loader in loaders) {
            try {
                val resources = loader.getResources(name)
                while (resources.hasMoreElements()) {
                    urlList.add(resources.nextElement())
                }
            } catch (_: Exception) {
            }
        }
        return Collections.enumeration(urlList)
    }

    public override fun loadClass(name: String, resolve: Boolean): Class<*> {
        for (loader in loaders) {
            try {
                return loader.loadClass(name)
            } catch (_: Exception) {
            }
        }
        throw ClassNotFoundException(name)
    }

    fun addClassLoader(classLoader: ClassLoader) {
        loaders.add(classLoader)
    }
}