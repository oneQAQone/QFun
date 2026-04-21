package me.yxp.qfun.ui.components.molecules

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.InnerShadow
import me.yxp.qfun.ui.core.theme.QFunTheme
import kotlin.math.abs
import kotlin.math.min

@Composable
fun FloatingLiquidTabs(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    val hapticFeedback = LocalHapticFeedback.current
    val density = LocalDensity.current
    val isGlassSupported = Build.VERSION.SDK_INT >= 31

    val headPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.70f, stiffness = 850f),
        label = "Head"
    )
    val tailPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 160f),
        label = "Tail"
    )

    val stretchAmount = remember(headPosition, tailPosition) { abs(headPosition - tailPosition) }
    val leftBoundary = min(headPosition, tailPosition)

    val trackWidth = 200.dp 
    val trackHeight = 44.dp
    val segmentWidth = trackWidth / options.size 

    val widthFactor = 0.72f + (stretchAmount * 0.28f).coerceIn(0f, 0.28f)
    val currentSliderWidth = segmentWidth * widthFactor
    val centeringOffset = (segmentWidth * (1f - widthFactor)) / 2f
    val currentSliderOffset = (segmentWidth * leftBoundary) + centeringOffset

    val tabsBackdrop = rememberLayerBackdrop()

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .drawBackdrop(
                    backdrop = backdrop,
                    exportedBackdrop = tabsBackdrop,
                    shape = { CircleShape },
                    effects = {
                        if (isGlassSupported) {
                            vibrancy()
                            blur(with(density) { 12f.dp.toPx() })
                        }
                    },
                    onDrawSurface = {
                        val containerColor = if (colors.isDark) Color(0xFF1C1C1E).copy(alpha = 0.5f) 
                                             else Color(0xFFFFFFFF).copy(alpha = 0.6f)
                        drawRect(containerColor)
                        drawRect(
                            color = Color.White.copy(alpha = if (colors.isDark) 0.05f else 0.2f),
                            style = Stroke(width = 0.5.dp.toPx())
                        )
                    }
                )
        ) {}

        Row(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .alpha(0f)
                .then(if (isGlassSupported) Modifier.layerBackdrop(tabsBackdrop) else Modifier)
        ) {
            options.forEach { title ->
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (colors.isDark) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (0.5 + stretchAmount * 2.2).sp 
                    )
                }
            }
        }

        val currentSliderHeight = 36.dp + (stretchAmount * 12).dp
        
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = currentSliderOffset)
                .width(currentSliderWidth)
                .height(currentSliderHeight)
                .drawBackdrop(
                    backdrop = if (isGlassSupported) rememberCombinedBackdrop(backdrop, tabsBackdrop) else backdrop,
                    shape = { CircleShape },
                    effects = {
                        if (isGlassSupported) {
                            lens(
                                refractionHeight = with(density) { 6f.dp.toPx() },
                                refractionAmount = with(density) { 8f.dp.toPx() }
                            )
                        }
                    },
                    innerShadow = { 
                        if (isGlassSupported) {
                            InnerShadow(
                                radius = 4.dp,
                                color = Color.White.copy(alpha = 0.2f),
                                alpha = 0.4f
                            )
                        } else null
                    },
                    onDrawSurface = {
                        val sliderColor = if (colors.isDark) Color.White.copy(0.12f)
                                          else Color.Black.copy(0.04f)
                        drawRect(sliderColor)
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (colors.isDark) 0.2f else 0.4f),
                                    Color.Transparent
                                )
                            ),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                )
        )

        Row(modifier = Modifier.width(trackWidth).height(trackHeight)) {
            options.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                val tabTextColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (colors.isDark) Color.White else colors.accentBlue 
                    } else {
                        if (colors.isDark) Color.White.copy(alpha = 0.45f) 
                        else Color.Black.copy(alpha = 0.45f)
                    },
                    animationSpec = tween(350),
                    label = "TextFade"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (!isSelected) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onOptionSelected(index)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = tabTextColor,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        letterSpacing = (0.5 + stretchAmount * 2.2).sp 
                    )
                }
            }
        }
    }
}