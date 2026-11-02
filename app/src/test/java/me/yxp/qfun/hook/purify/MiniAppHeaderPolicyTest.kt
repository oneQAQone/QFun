package me.yxp.qfun.hook.purify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MiniAppHeaderPolicyTest {

    @Test
    fun usesVersionSpecificTwoLevelField() {
        assertEquals("D", miniAppTwoLevelFieldName(8537, false))
        assertEquals("E", miniAppTwoLevelFieldName(8538, false))
        assertEquals("E", miniAppTwoLevelFieldName(9897, false))
        assertEquals("I", miniAppTwoLevelFieldName(9898, false))
        assertEquals("I", miniAppTwoLevelFieldName(15389, false))
        assertEquals("t", miniAppTwoLevelFieldName(15390, false))
        assertEquals("t", miniAppTwoLevelFieldName(16580, false))
        assertEquals("D", miniAppTwoLevelFieldName(16580, true))
    }

    @Test
    fun leavesNewReceivingIndicatorLifecycleUntouched() {
        assertTrue(miniAppNeedsLegacyRefreshHook(15389))
        assertFalse(miniAppNeedsLegacyRefreshHook(15390))
        assertFalse(miniAppNeedsLegacyRefreshHook(16580))
    }

    @Test
    fun findsEnableFlagOnTwoLevelHeaderInsteadOfShadowedField() {
        val field = findMiniAppTwoLevelField(Header::class.java, 16580, false)
        assertEquals(TwoLevelHeader::class.java, field.declaringClass)
        val header = Header()
        field.setBoolean(header, false)
        assertFalse(header.enabled())
    }

    @Test
    fun toggleRestoresOriginalHeaderWithoutRecreatingIt() {
        val header = Header()
        val state = MiniAppHeaderState(findMiniAppTwoLevelField(Header::class.java, 16580, false))
        state.apply(header, false)
        assertTrue(header.enabled())
        state.apply(header, true)
        state.apply(header, true)
        assertFalse(header.enabled())
        state.apply(header, false)
        assertTrue(header.enabled())
    }

    @Test
    fun disablingDoesNotEnableAHeaderOriginallyDisabledByQQ() {
        val header = Header()
        val field = findMiniAppTwoLevelField(Header::class.java, 16580, false)
        field.setBoolean(header, false)
        val state = MiniAppHeaderState(field)
        state.apply(header, true)
        state.apply(header, false)
        assertFalse(header.enabled())
    }

    @Test
    fun disabledCallbacksPreserveChangesMadeByQQ() {
        val header = Header()
        val field = findMiniAppTwoLevelField(Header::class.java, 16580, false)
        val state = MiniAppHeaderState(field)
        state.apply(header, false)
        field.setBoolean(header, false)
        state.apply(header, false)
        assertFalse(header.enabled())
    }

    open class TwoLevelHeader {
        private var t: Boolean = true
        fun enabled() = t
    }
    class Header : TwoLevelHeader() {
        private val t: String = "unrelated"
    }
}
