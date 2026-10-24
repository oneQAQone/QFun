package me.yxp.qfun.hook.chat

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeRepeatGestureTest {

    private fun gesture() = SwipeRepeatGesture(8f, 64f, 500L).apply { begin(100f, 100f, 0L) }

    @Test
    fun rightSwipeSendsOnceOnRelease() {
        val gesture = gesture()
        assertTrue(gesture.move(180f, 102f, 100L))
        assertTrue(gesture.ready)
        assertTrue(gesture.release())
        assertFalse(gesture.release())
    }

    @Test
    fun leftAndVerticalGesturesCannotTurnIntoRepeat() {
        for ((x, y) in listOf(80f to 100f, 100f to 120f, 120f to 120f)) {
            val gesture = gesture()
            assertFalse(gesture.move(x, y, 100L))
            assertFalse(gesture.move(220f, 100f, 200L))
            assertFalse(gesture.release())
        }
    }

    @Test
    fun shortSwipeAndDraggingBackBelowThresholdDoNotSend() {
        val gesture = gesture()
        assertTrue(gesture.move(140f, 100f, 100L))
        assertFalse(gesture.ready)
        assertTrue(gesture.move(200f, 100f, 200L))
        assertTrue(gesture.ready)
        assertTrue(gesture.move(150f, 100f, 300L))
        assertFalse(gesture.release())
    }

    @Test
    fun cancellationAndLongPressNeverSend() {
        val cancelled = gesture()
        cancelled.move(200f, 100f, 100L)
        cancelled.cancel()
        assertFalse(cancelled.release())

        val held = gesture()
        assertFalse(held.move(200f, 100f, 500L))
        assertFalse(held.release())
    }

    @Test
    fun stationaryJitterDoesNotChooseDirectionAndNewDownClearsOldState() {
        val gesture = gesture()
        assertFalse(gesture.move(104f, 98f, 50L))
        assertTrue(gesture.move(200f, 100f, 100L))
        gesture.begin(200f, 200f, 200L)
        assertFalse(gesture.ready)
        assertFalse(gesture.release())
    }
}
