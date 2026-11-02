package me.yxp.qfun.utils.reflect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReflectVisibilityTest {
    private val target = VisibilityFixture::class.java
    private val names = mapOf(
        Visibility.PUBLIC to "public",
        Visibility.PROTECTED to "protected",
        Visibility.PRIVATE to "private",
        Visibility.PACKAGE to "package"
    )

    @Test
    fun visibilityFiltersMethodsAndFieldsConsistently() {
        for ((expectedVisibility, prefix) in names) {
            assertEquals("${prefix}Method", target.findMethod { visibility = expectedVisibility }.name)
            assertEquals("${prefix}Field", target.findField { visibility = expectedVisibility }.name)
            for (otherVisibility in Visibility.entries.filter { it != expectedVisibility }) {
                assertNull(target.findMethodOrNull {
                    name = "${prefix}Method"
                    visibility = otherVisibility
                })
                assertNull(target.findFieldOrNull {
                    name = "${prefix}Field"
                    visibility = otherVisibility
                })
            }
        }
    }

    @Test
    fun unrestrictedSearchStillFindsPrivateMembers() {
        assertNotNull(target.findMethodOrNull { name = "privateMethod" })
        assertNotNull(target.findFieldOrNull { name = "privateField" })
    }
}
