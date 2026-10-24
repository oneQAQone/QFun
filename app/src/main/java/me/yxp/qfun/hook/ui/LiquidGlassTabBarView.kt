package me.yxp.qfun.hook.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.tencent.mobileqq.vas.theme.api.ThemeUtil
import me.yxp.qfun.utils.qq.QQCurrentEnv
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import android.graphics.Color as AndroidColor

private const val SAMPLE_SCALE = 0.5f
private const val IDLE_FALLBACK_MS = 3000L

private val tabNameMap = mapOf(
    "com.tencent.mobileqq.activity.home.Conversation" to "消息",
    "com.tencent.mobileqq.activity.contacts.base.Contacts" to "联系人",
    "com.tencent.mobileqq.guild.mainframe.GuildFragmentDelegateFrame" to "频道",
    "com.tencent.mobileqq.leba.Leba" to "动态",
    "com.tencent.mobileqq.activity.qcircle.QCircleFrame" to "小世界",
    "com.tencent.mobileqq.activity.qqsettingmev3.MeFrame" to "我的",
    "com.tencent.mobileqq.activity.tim.mine.Mine" to "我的",
    "com.tencent.mobileqq.ai.AIAssistantFrame" to "AI助手",
    "com.tencent.mobileqq.activity.leba.QzoneFrame" to "QQ空间",
    "com.tencent.mobileqq.gamecenter.qa.metadream.MetaDreamFrame" to "元梦之星",
)

private class GlassSampler {

    private val samplePaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null
    private var sampleW = 0
    private var sampleH = 0
    private var dirty = true
    private var lastRefreshAt = 0L
    private var suppressDirty = false

    var isActive = true

    fun markDirty() {
        if (suppressDirty) return
        dirty = true
    }

    fun activate() {
        dirty = true
        lastRefreshAt = 0L
        isActive = true
    }

    fun deactivate() {
        isActive = false
    }

    fun needsRefresh(now: Long): Boolean {
        if (!isActive) return false
        if (lastRefreshAt == 0L) return true
        return dirty || now - lastRefreshAt >= IDLE_FALLBACK_MS
    }

    fun refresh(
        pageRootView: View,
        composeView: View,
        width: Int,
        height: Int,
        deltaX: Int,
        deltaY: Int
    ): Boolean {
        if (width <= 0 || height <= 0) return false
        val sw = (width * SAMPLE_SCALE).toInt().coerceAtLeast(1)
        val sh = (height * SAMPLE_SCALE).toInt().coerceAtLeast(1)

        var bmp = bitmap
        if (bmp == null || sampleW != sw || sampleH != sh) {
            bitmap?.recycle()
            bmp = createBitmap(sw, sh)
            bitmap = bmp
            bitmapCanvas = Canvas(bmp)
            sampleW = sw
            sampleH = sh
        }
        val canvas = bitmapCanvas ?: return false

        canvas.drawColor(AndroidColor.TRANSPARENT, PorterDuff.Mode.CLEAR)
        suppressDirty = true
        val previous = composeView.visibility
        try {
            composeView.visibility = View.INVISIBLE
            canvas.withScale(SAMPLE_SCALE, SAMPLE_SCALE) {
                translate(-deltaX.toFloat(), -deltaY.toFloat())
                pageRootView.draw(this)
            }
        } finally {
            composeView.visibility = previous
            suppressDirty = false
        }

        dirty = false
        lastRefreshAt = SystemClock.uptimeMillis()
        return true
    }

    fun draw(canvas: ComposeCanvas, width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        val bmp = bitmap ?: return
        canvas.nativeCanvas.drawBitmap(bmp, null, Rect(0, 0, width, height), samplePaint)
    }

    fun dispose() {
        bitmap?.recycle()
        bitmap = null
        bitmapCanvas = null
    }
}

@Composable
fun LiquidGlassTabBarContent(
    tabTags: List<String>,
    currentTag: String,
    badgeTexts: Map<Int, String>,
    pageRootView: View,
    onTabSelected: (index: Int, tag: String) -> Unit
) {
    val isDark = ThemeUtil.isInNightMode(QQCurrentEnv.qQAppInterface)
    val baseBackdrop = rememberLayerBackdrop()
    val currentComposeView = LocalView.current
    val context = LocalContext.current

    val sampler = remember { GlassSampler() }
    var redrawTrigger by remember { mutableIntStateOf(0) }

    val rootLoc = remember { IntArray(2) }
    val capsuleLoc = remember { IntArray(4) }

    DisposableEffect(pageRootView) {
        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = sampler.activate()
            override fun onViewDetachedFromWindow(v: View) = sampler.deactivate()
        }
        val focusListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) sampler.activate() else sampler.deactivate()
        }
        pageRootView.addOnAttachStateChangeListener(attachListener)
        pageRootView.viewTreeObserver.addOnWindowFocusChangeListener(focusListener)
        onDispose {
            pageRootView.removeOnAttachStateChangeListener(attachListener)
            if (pageRootView.viewTreeObserver.isAlive) {
                pageRootView.viewTreeObserver.removeOnWindowFocusChangeListener(focusListener)
            }
        }
    }

    DisposableEffect(pageRootView) {
        val scrollListener = ViewTreeObserver.OnScrollChangedListener { sampler.markDirty() }
        val layoutListener = ViewTreeObserver.OnGlobalLayoutListener { sampler.markDirty() }

        var registered = false
        val preDrawListener = ViewTreeObserver.OnPreDrawListener {
            if (!pageRootView.isShown) {
                sampler.deactivate()
                return@OnPreDrawListener true
            }
            if (sampler.needsRefresh(SystemClock.uptimeMillis())) {
                val w = capsuleLoc[2] - capsuleLoc[0]
                val h = capsuleLoc[3] - capsuleLoc[1]
                if (w > 0 && h > 0) {
                    pageRootView.getLocationInWindow(rootLoc)
                    sampler.refresh(
                        pageRootView,
                        currentComposeView,
                        w,
                        h,
                        capsuleLoc[0] - rootLoc[0],
                        capsuleLoc[1] - rootLoc[1]
                    )
                    redrawTrigger++
                }
            }
            true
        }

        fun attachPreDraw() {
            if (registered) return
            val observer = pageRootView.viewTreeObserver
            if (observer.isAlive) {
                observer.addOnPreDrawListener(preDrawListener)
                registered = true
            }
        }

        fun detachPreDraw() {
            if (!registered) return
            val observer = pageRootView.viewTreeObserver
            if (observer.isAlive) observer.removeOnPreDrawListener(preDrawListener)
            registered = false
        }

        pageRootView.viewTreeObserver.let { observer ->
            observer.addOnScrollChangedListener(scrollListener)
            observer.addOnGlobalLayoutListener(layoutListener)
        }

        val gate = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        sampler.deactivate()
                        detachPreDraw()
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        sampler.activate()
                        attachPreDraw()
                    }
                }
            }
        }
        context.registerReceiver(
            gate,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
        )

        attachPreDraw()

        onDispose {
            detachPreDraw()
            context.unregisterReceiver(gate)
            val observer = pageRootView.viewTreeObserver
            if (observer.isAlive) {
                observer.removeOnScrollChangedListener(scrollListener)
                observer.removeOnGlobalLayoutListener(layoutListener)
            }
        }
    }

    DisposableEffect(sampler) {
        onDispose { sampler.dispose() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInWindow()
                    capsuleLoc[0] = bounds.left.roundToInt()
                    capsuleLoc[1] = bounds.top.roundToInt()
                    capsuleLoc[2] = bounds.right.roundToInt()
                    capsuleLoc[3] = bounds.bottom.roundToInt()
                    sampler.markDirty()
                }
                .alpha(0f)
                .layerBackdrop(baseBackdrop)
                .drawWithContent {
                    redrawTrigger.let {
                        drawIntoCanvas { canvas ->
                            sampler.draw(
                                canvas,
                                size.width.roundToInt(),
                                size.height.roundToInt()
                            )
                        }
                    }
                }
        )

        val selectedIndex = tabTags.indexOf(currentTag).coerceAtLeast(0)
        val tabCount = tabTags.size.coerceAtLeast(1)
        val contentColor =
            if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF1D1D1F).copy(alpha = 0.85f)

        LiquidBottomTabs(
            selectedTabIndex = { selectedIndex },
            onTabSelected = { index ->
                if (index in tabTags.indices) {
                    onTabSelected(index, tabTags[index])
                }
            },
            backdrop = baseBackdrop,
            tabsCount = tabCount
        ) {
            tabTags.forEachIndexed { index, tag ->
                LiquidBottomTab(
                    onClick = {
                        if (index in tabTags.indices) {
                            onTabSelected(index, tabTags[index])
                        }
                    }
                ) {
                    val badgeText = badgeTexts[index]
                    val title = tabNameMap[tag] ?: tag.substringAfterLast('.')

                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )

                        if (!badgeText.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-7.5).dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(contentColor.copy(alpha = 0.12f))
                                    .border(
                                        0.75.dp,
                                        contentColor.copy(alpha = 0.55f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = contentColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
