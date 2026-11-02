package me.yxp.qfun.hook.purify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QZoneAdViewStateTest {

    @Test
    fun capturesLayoutAssignedAfterConstructionBeforeItIsCollapsed() {
        val state = QZoneAdViewState(0)
        state.rememberDimensions(null, null)
        assertNull(state.width)
        state.rememberDimensions(1200, 1138)
        state.rememberDimensions(0, 0)
        assertEquals(1200, state.width)
        assertEquals(1138, state.height)
    }

    @Test
    fun preservesWrapContentAndMatchParentInsteadOfCollapsedDimensions() {
        val state = QZoneAdViewState(4)
        state.rememberDimensions(-1, -2)
        state.rememberDimensions(0, 0)
        assertEquals(-1, state.width)
        assertEquals(-2, state.height)
        assertEquals(4, state.visibility)
    }
}
