package me.yxp.qfun.hook.purify

import org.junit.Assert.assertEquals
import org.junit.Test

class MiniAppPullDistanceTest {
    @Test
    fun longPullStopsAtShortenedDistance() {
        val density = 3.5f
        val pullLimit = (60 * density).toInt()
        val limited = limitMiniAppPullOffset(1800, density)
        assertEquals(pullLimit, limited)
        assertEquals(limited, limitMiniAppPullOffset(2400, density))
    }

    @Test
    fun preservesShortPullsReboundAndUpwardScrolling() {
        for (offset in listOf(-1200, -1, 0, 1, 180, 210)) {
            assertEquals(offset, limitMiniAppPullOffset(offset, 3.5f))
        }
    }
}
