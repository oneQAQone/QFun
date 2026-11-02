package me.yxp.qfun.hook.purify

import android.widget.RelativeLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class MiniAppContentTargetTest {

    @Test
    fun findsMiniAppPanelOnParentInsteadOfRefreshIndicator() {
        val field = findMiniAppContentField(Header::class.java)
        assertEquals(QQChatListTwoLevelHeader::class.java, field.declaringClass)
        assertEquals("miniAppPanel", field.name)
    }

    @Test
    fun findsInheritedMovementCallbacksAndExcludesOtherSignatures() {
        val methods = findMiniAppMovingCallbacks(Header::class.java)
        assertEquals(listOf("onHeaderMoving"), methods.map { it.name })
    }

    open class QQChatListTwoLevelHeader {
        private val miniAppPanel: RelativeLayout? = null
        private val refreshIndicator: Any? = null
        fun onHeaderMoving(header: Any?, dragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) = Unit
        fun onRefreshStateChanged(layout: Any?, old: Any?, new: Any?) = Unit
    }

    class Header : QQChatListTwoLevelHeader() {
        private val unrelatedPanel: RelativeLayout? = null
    }
}
