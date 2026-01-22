package me.yxp.qfun.lifecycle

import android.app.Activity
import java.util.concurrent.ConcurrentHashMap

object DynamicActivityRegistry {
    
    private val registry = ConcurrentHashMap<String, Class<out Activity>>()

    @Suppress("UNCHECKED_CAST")
    fun register(clazz: Class<*>) {
        registry[clazz.name] = clazz as Class<out Activity>
    }

    fun unregister(className: String) {
        registry.remove(className)
    }

    fun contains(className: String): Boolean {
        return registry.containsKey(className)
    }

    fun getActivityClass(className: String): Class<out Activity>? {
        return registry[className]
    }
}