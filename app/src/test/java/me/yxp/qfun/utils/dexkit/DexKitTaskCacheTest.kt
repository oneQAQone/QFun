package me.yxp.qfun.utils.dexkit

import org.junit.Assert.assertEquals
import org.junit.Test
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher

class DexKitTaskCacheTest {
    @Test
    fun validClassAndMethodCacheNeedsNoNativeDexScan() {
        val lookedUp = mutableListOf<String>()
        val task = object : DexKitTask {
            override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
                "class" to FindClass(), "method" to FindMethod()
            )
            override fun requireClass(name: String): Class<*> {
                lookedUp.add(name)
                return String::class.java
            }
            override fun requireMethod(name: String): java.lang.reflect.Method {
                lookedUp.add(name)
                return String::class.java.getMethod("length")
            }
        }
        // A native scan would fail in a local JVM test; valid caches must return before it.
        task.resolveMissingQueries()
        assertEquals(listOf("class", "method"), lookedUp)
    }

    @Test
    fun emptyTaskNeedsNoHostInitialization() {
        val task = object : DexKitTask {
            override fun getQueryMap(): Map<String, BaseMatcher> = emptyMap()
        }
        task.resolveMissingQueries()
    }
}
