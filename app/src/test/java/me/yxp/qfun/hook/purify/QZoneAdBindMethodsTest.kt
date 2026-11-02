package me.yxp.qfun.hook.purify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class QZoneAdBindMethodsTest {

    @Test
    fun findsBindingInSharedSuperclassOnlyOnce() {
        val methods = findQZoneAdBindMethods(listOf(PictureAd::class.java, VideoAd::class.java))
        assertEquals(1, methods.size)
        assertEquals(BaseAd::class.java.getDeclaredMethod("bindData", Any::class.java, Int::class.javaPrimitiveType), methods.single())
    }

    @Test
    fun includesOverridesAndExcludesOtherSignatures() {
        val methods = findQZoneAdBindMethods(listOf(OverridingAd::class.java))
        assertEquals(setOf(BaseAd::class.java, OverridingAd::class.java), methods.map { it.declaringClass }.toSet())
        assertFalse(methods.any { it.parameterTypes[1] == String::class.java })
    }

    @Test
    fun handlesNoClassesAndNoBindingMethods() {
        assertEquals(emptyList<java.lang.reflect.Method>(), findQZoneAdBindMethods(emptyList()))
        assertEquals(emptyList<java.lang.reflect.Method>(), findQZoneAdBindMethods(listOf(Any::class.java)))
    }

    @Test
    fun skipsAbstractWidgetBindingContract() {
        val methods = findQZoneAdBindMethods(listOf(ConcreteAd::class.java))
        assertEquals(listOf(ConcreteAd::class.java), methods.map { it.declaringClass })
    }

    abstract class AbstractAd {
        abstract fun bindData(data: Any?, position: Int)
    }

    class ConcreteAd : AbstractAd() {
        override fun bindData(data: Any?, position: Int) = Unit
    }

    open class BaseAd {
        open fun bindData(data: Any?, position: Int) = Unit
        fun bindData(data: Any?, position: String) = Unit
    }

    class PictureAd : BaseAd()
    class VideoAd : BaseAd()
    class OverridingAd : BaseAd() {
        override fun bindData(data: Any?, position: Int) = Unit
    }
}
