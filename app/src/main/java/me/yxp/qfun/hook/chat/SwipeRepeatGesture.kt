package me.yxp.qfun.hook.chat

import kotlin.math.abs

internal class SwipeRepeatGesture(
    private val touchSlop: Float,
    private val threshold: Float,
    private val longPressTimeout: Long,
) {

    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var tracking = false

    var dragging = false
        private set

    var distance = 0f
        private set

    val ready: Boolean get() = dragging && distance >= threshold

    fun begin(x: Float, y: Float, time: Long) {
        cancel()
        startX = x
        startY = y
        startTime = time
        tracking = true
    }

    fun move(x: Float, y: Float, time: Long): Boolean {
        if (!tracking) return false
        val dx = x - startX
        val dy = y - startY
        if (!dragging) {
            if (time - startTime >= longPressTimeout) {
                cancel()
                return false
            }
            if (abs(dx) <= touchSlop && abs(dy) <= touchSlop) return false
            if (dx <= touchSlop || dx <= abs(dy) * 2f) {
                cancel()
                return false
            }
            dragging = true
        }
        distance = (dx - touchSlop).coerceAtLeast(0f)
        return true
    }

    fun release(): Boolean {
        val repeat = ready
        cancel()
        return repeat
    }

    fun cancel() {
        tracking = false
        dragging = false
        distance = 0f
    }
}
