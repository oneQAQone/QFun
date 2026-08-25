package me.yxp.qfun.hook.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
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
import android.graphics.Color as AndroidColor

private val tabNameMap = mapOf(
    "com.tencent.mobileqq.activity.home.Conversation" to "消息",
    "com.tencent.mobileqq.activity.contacts.base.Contacts" to "联系人",
    "com.tencent.mobileqq.guild.mainframe.GuildFragmentDelegateFrame" to "频道",
    "com.tencent.mobileqq.leba.Leba" to "动态",
    "com.tencent.mobileqq.activity.qcircle.QCircleFrame" to "小世界",
    "com.tencent.mobileqq.activity.qqsettingmev3.MeFrame" to "我的",
    "com.tencent.mobileqq.ai.AIAssistantFrame" to "AI助手",
    "com.tencent.mobileqq.activity.leba.QzoneFrame" to "QQ空间",
    "com.tencent.mobileqq.gamecenter.qa.metadream.MetaDreamFrame" to "元梦之星",
)

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

    var redrawTrigger by remember { mutableLongStateOf(0L) }
    DisposableEffect(pageRootView) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            if (pageRootView.isShown) redrawTrigger++
            true
        }
        pageRootView.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            pageRootView.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }

    val rootLoc = remember { IntArray(2) }
    val capsuleLoc = remember { IntArray(2) }

    var sampleBmp by remember { mutableStateOf<Bitmap?>(null) }
    var sampleCanvas by remember { mutableStateOf<Canvas?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            sampleBmp?.recycle()
            sampleBmp = null
            sampleCanvas = null
        }
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
                }
                .alpha(0f)
                .layerBackdrop(baseBackdrop)
                .drawWithContent {
                    redrawTrigger.let {
                        val w = size.width.roundToInt()
                        val h = size.height.roundToInt()
                        if (w > 0 && h > 0) {
                            val scale = 0.5f
                            val sw = (w * scale).toInt()
                            val sh = (h * scale).toInt()

                            if (sampleBmp?.width != sw || sampleBmp?.height != sh) {
                                sampleBmp?.recycle()
                                sampleBmp = createBitmap(sw, sh).also {
                                    sampleCanvas = Canvas(it)
                                }
                            }

                            val bmp = sampleBmp
                            val cvs = sampleCanvas
                            if (bmp != null && cvs != null) {
                                pageRootView.getLocationInWindow(rootLoc)
                                val deltaX = (capsuleLoc[0] - rootLoc[0]).toFloat()
                                val deltaY = (capsuleLoc[1] - rootLoc[1]).toFloat()

                                cvs.drawColor(AndroidColor.TRANSPARENT, PorterDuff.Mode.CLEAR)
                                cvs.withScale(scale, scale) {
                                    translate(-deltaX, -deltaY)
                                    currentComposeView.visibility = View.INVISIBLE
                                    pageRootView.draw(this)
                                    currentComposeView.visibility = View.VISIBLE
                                }

                                drawImage(
                                    image = bmp.asImageBitmap(),
                                    dstSize = IntSize(w, h),
                                    filterQuality = FilterQuality.Medium
                                )
                            }
                        }
                    }
                }
        )

        val selectedIndex = tabTags.indexOf(currentTag).coerceAtLeast(0)
        val tabCount = tabTags.size.coerceAtLeast(1)
        val contentColor = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF1D1D1F).copy(alpha = 0.85f)

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
                                    .border(0.75.dp, contentColor.copy(alpha = 0.55f), RoundedCornerShape(50))
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