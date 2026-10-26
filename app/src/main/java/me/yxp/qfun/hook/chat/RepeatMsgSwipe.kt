package me.yxp.qfun.hook.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.toClass
import me.yxp.qfun.utils.ui.ThemeHelper
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.WeakHashMap
import kotlin.math.min
import kotlin.math.roundToInt

internal object RepeatMsgSwipe {

    private const val BUBBLE_VB = "com.tencent.mobileqq.aio.msglist.holder.AIOBubbleMsgItemVB"
    private const val SWIPE_HELPER =
        "com.tencent.mobileqq.aio.msglist.holder.component.leftswipearea.AIOContentLeftSwipeHelper"

    fun install(
        owner: BaseSwitchHookItem,
        canRepeat: (MsgRecord) -> Boolean,
        repeat: (MsgRecord) -> Unit
    ) {
        val holderClass = BUBBLE_VB.toClass
        val msgItemClass = AIOMsgItem::class.java
        val helperClass = SWIPE_HELPER.toClass

        val messageField = holderClass.findExactField(msgItemClass)
        val lazyField = holderClass.findExactField(
            Class.forName("kotlin.Lazy", false, holderClass.classLoader)
        )
        val containerField = helperClass.findExactField(ViewGroup::class.java)
        val contentField = helperClass.findExactField(View::class.java)

        val recordMethod = msgItemClass.findMethod { name = "getMsgRecord" }
        val sessions = WeakHashMap<Any, SwipeSession>()

        fun currentMessage(instance: Any): MsgRecord? = runCatching {
            val item = messageField.get(instance) ?: return@runCatching null
            recordMethod.invoke(item) as? MsgRecord
        }.getOrNull()

        val intercept = holderClass.findMethod {
            name = "onInterceptTouchEvent"
            paramTypes(MotionEvent::class.java)
        }
        val touch = holderClass.findMethod {
            name = "onTouchEvent"
            paramTypes(MotionEvent::class.java)
        }

        intercept.hookBefore(owner) { param ->
            val event = param.args[0] as? MotionEvent ?: return@hookBefore
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                sessions.remove(param.thisObject)?.reset()
            }
        }

        intercept.hookAfter(owner) { param ->
            val instance = param.thisObject
            val event = param.args[0] as? MotionEvent ?: return@hookAfter
            val current = sessions[instance]

            if (event.actionMasked == MotionEvent.ACTION_DOWN && param.result == false) {
                val msg = currentMessage(instance) ?: return@hookAfter
                if (!canRepeat(msg)) return@hookAfter
                val nativeHelper = lazyField.get(instance)?.callMethod("getValue")
                    ?: return@hookAfter
                val container = containerField.get(nativeHelper) as? ViewGroup ?: return@hookAfter
                val content = contentField.get(nativeHelper) as? View ?: return@hookAfter
                if (container.scrollX != 0 || !content.isShown) return@hookAfter
                if (event.x !in content.x..(content.x + content.width) ||
                    event.y !in content.y..(content.y + content.height)
                ) return@hookAfter
                sessions[instance] = SwipeSession(container, content, msg).also { it.begin(event) }
                return@hookAfter
            }

            if (current == null) return@hookAfter

            if (current.captured) {
                if (event.pointerCount != 1) current.cancelDrag()
                else if (event.actionMasked == MotionEvent.ACTION_MOVE) current.move(event)
                param.result = true
            } else if (event.pointerCount != 1 || param.result == true) {
                current.reset()
            } else if (event.actionMasked == MotionEvent.ACTION_MOVE && current.move(event)) {
                param.result = true
            } else if (event.actionMasked == MotionEvent.ACTION_CANCEL ||
                event.actionMasked == MotionEvent.ACTION_UP
            ) {
                current.reset()
            }
        }

        touch.hookBefore(owner) { param ->
            val session = sessions[param.thisObject] ?: return@hookBefore
            if (!session.captured) return@hookBefore
            val event = param.args[0] as? MotionEvent ?: return@hookBefore
            param.result = true
            when {
                event.actionMasked == MotionEvent.ACTION_CANCEL -> session.finish(false)
                event.pointerCount != 1 || event.actionMasked == MotionEvent.ACTION_POINTER_UP ->
                    session.cancelDrag()

                event.actionMasked == MotionEvent.ACTION_UP -> {
                    session.move(event)
                    val msg = currentMessage(param.thisObject)
                    val send = msg != null && session.matches(msg) && canRepeat(msg)
                    val done = session.finish(send)
                    if (done && msg != null) repeat(msg)
                }

                event.actionMasked == MotionEvent.ACTION_MOVE -> session.move(event)
            }
        }

        holderClass.findMethodsForRebind(msgItemClass).forEach { method ->
            method.hookBefore(owner) { param -> sessions.remove(param.thisObject)?.reset() }
        }
    }

    private fun Class<*>.findExactField(fieldType: Class<*>): java.lang.reflect.Field {
        return declaredFields.single {
            it.type == fieldType && !Modifier.isStatic(it.modifiers)
        }.apply { isAccessible = true }
    }

    private fun Class<*>.findMethodsForRebind(msgItemClass: Class<*>): List<Method> {
        return declaredMethods.filter { method ->
            Modifier.isPublic(method.modifiers) &&
                    !Modifier.isStatic(method.modifiers) &&
                    method.returnType == Void.TYPE &&
                    method.parameterTypes.contentEquals(arrayOf(msgItemClass))
        }.onEach { it.isAccessible = true }
    }

    private class SwipeSession(container: ViewGroup, content: View, msg: MsgRecord) :
        View.OnAttachStateChangeListener {

        private val containerRef = WeakReference(container)
        private val contentRef = WeakReference(content)
        private val msgId = msg.msgId
        private val peerUid = msg.peerUid
        private val chatType = msg.chatType
        private val density = container.resources.displayMetrics.density
        private val threshold = 64f * density
        private val gesture = SwipeRepeatGesture(
            ViewConfiguration.get(container.context).scaledTouchSlop.toFloat(),
            threshold,
            ViewConfiguration.getLongPressTimeout().toLong(),
        )
        private var overlayRef: WeakReference<ViewGroup>? = null
        private val badge =
            RepeatBadge(density, runCatching { ThemeHelper.isNightMode() }.getOrDefault(false))
        private var animator: ValueAnimator? = null
        private var wasReady = false
        private var ownsScroll = false

        var captured = false
            private set

        fun matches(msg: MsgRecord): Boolean =
            msgId == msg.msgId && peerUid == msg.peerUid && chatType == msg.chatType

        fun begin(event: MotionEvent) {
            containerRef.get()?.addOnAttachStateChangeListener(this)
            gesture.begin(event.x, event.y, event.eventTime)
        }

        fun move(event: MotionEvent): Boolean {
            if (!gesture.move(event.x, event.y, event.eventTime)) return false
            val container = containerRef.get() ?: return false
            if (!ownsScroll) {
                ownsScroll = true
                captured = true
                container.cancelLongPress()
                contentRef.get()?.cancelLongPress()
                (container.parent as? ViewGroup)?.let { parent ->
                    overlayRef = WeakReference(parent)
                    parent.overlay.add(badge)
                }
            }
            container.parent?.requestDisallowInterceptTouchEvent(true)
            val distance = gesture.distance
            val displayed = if (distance <= threshold) distance
            else min(threshold + (distance - threshold) * 0.35f, 104f * density)
            render(displayed, gesture.ready)
            if (gesture.ready && !wasReady) {
                container.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
            wasReady = gesture.ready
            return true
        }

        fun finish(allowRepeat: Boolean): Boolean {
            captured = false
            val send = gesture.release() && allowRepeat
            containerRef.get()?.parent?.requestDisallowInterceptTouchEvent(false)
            val start = -(containerRef.get()?.scrollX ?: 0).toFloat()
            animator = ValueAnimator.ofFloat(start, 0f).apply {
                duration = 160L
                interpolator = DecelerateInterpolator()
                addUpdateListener { render(it.animatedValue as Float, false) }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) = reset()
                })
                start()
            }
            return send
        }

        fun cancelDrag() {
            gesture.cancel()
            render(0f, false)
        }

        private fun render(distance: Float, ready: Boolean) {
            val container = containerRef.get() ?: return
            val parent = overlayRef?.get() ?: return
            if (container.parent !== parent) {
                reset()
                return
            }
            container.scrollTo(-distance.roundToInt(), 0)
            val content = contentRef.get() ?: return
            val rect = Rect(0, 0, content.width, content.height)
            parent.offsetDescendantRectToMyCoords(content, rect)
            val root = Rect(0, 0, container.width, container.height)
            parent.offsetDescendantRectToMyCoords(container, root)
            val radius = 20f * density
            val cx = root.left + container.scrollX + min(distance / 2f, 30f * density)
            val cy = rect.exactCenterY()
            badge.setBounds(
                (cx - radius).roundToInt(), (cy - radius).roundToInt(),
                (cx + radius).roundToInt(), (cy + radius).roundToInt()
            )
            badge.progress = (distance / threshold).coerceIn(0f, 1f)
            badge.ready = ready
            badge.invalidateSelf()
        }

        fun reset() {
            captured = false
            gesture.cancel()
            animator?.let {
                animator = null
                it.removeAllListeners()
                it.cancel()
            }
            overlayRef?.get()?.overlay?.remove(badge)
            overlayRef = null
            containerRef.get()?.let {
                if (ownsScroll) {
                    it.scrollTo(0, 0)
                    it.parent?.requestDisallowInterceptTouchEvent(false)
                }
                it.removeOnAttachStateChangeListener(this)
            }
            ownsScroll = false
        }

        override fun onViewAttachedToWindow(v: View) = Unit

        override fun onViewDetachedFromWindow(v: View) = reset()
    }

    private class RepeatBadge(density: Float, dark: Boolean) : Drawable() {

        private val unit = density
        private val discPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f * density
            strokeCap = Paint.Cap.ROUND
        }
        private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f * density
        }
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = 15f * density
            typeface = Typeface.DEFAULT_BOLD
        }
        private val arcBounds = RectF()
        private val accent = if (dark) Color.rgb(48, 209, 88) else Color.rgb(52, 199, 89)
        private val track = if (dark) Color.rgb(96, 116, 134) else Color.rgb(178, 196, 210)
        private val arc = if (dark) Color.rgb(169, 198, 220) else Color.rgb(78, 114, 144)
        private val ink = if (dark) Color.rgb(226, 238, 248) else Color.rgb(58, 88, 116)

        var progress = 0f
        var ready = false

        override fun draw(canvas: Canvas) {
            val value = progress.coerceIn(0f, 1f)
            val alpha = ((value - 0.10f) / 0.35f).coerceIn(0f, 1f) * 255f
            if (alpha <= 0f) return
            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()
            val radius = bounds.width() / 2f - ringPaint.strokeWidth / 2f
            val tone = blend(arc, accent, ((value - 0.55f) / 0.45f).coerceIn(0f, 1f))

            ringPaint.color = track
            ringPaint.alpha = (alpha * 0.32f).roundToInt()
            canvas.drawCircle(centerX, centerY, radius, ringPaint)

            discPaint.color = if (ready) accent else tone
            discPaint.alpha = if (ready) alpha.roundToInt() else (alpha * 0.16f).roundToInt()
            canvas.drawCircle(
                centerX,
                centerY,
                (radius - ringPaint.strokeWidth * 1.6f) * (0.88f + value * 0.12f),
                discPaint
            )

            if (ready) {
                glowPaint.color = accent
                glowPaint.alpha = (alpha * 0.42f).roundToInt()
                canvas.drawCircle(centerX, centerY, radius + unit * 2.1f, glowPaint)
            }

            ringPaint.color = tone
            ringPaint.alpha = alpha.roundToInt()
            arcBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            canvas.drawArc(arcBounds, -90f, 360f * value, false, ringPaint)

            textPaint.color = if (ready) Color.WHITE else ink
            textPaint.alpha = alpha.roundToInt()
            canvas.drawText(
                "+1",
                centerX,
                centerY - (textPaint.ascent() + textPaint.descent()) / 2f,
                textPaint
            )
        }

        private fun blend(from: Int, to: Int, ratio: Float): Int = Color.rgb(
            (Color.red(from) + (Color.red(to) - Color.red(from)) * ratio).roundToInt(),
            (Color.green(from) + (Color.green(to) - Color.green(from)) * ratio).roundToInt(),
            (Color.blue(from) + (Color.blue(to) - Color.blue(from)) * ratio).roundToInt()
        )

        override fun setAlpha(alpha: Int) = Unit

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit

        @Deprecated("Deprecated in Android", ReplaceWith("PixelFormat.TRANSLUCENT"))
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}
