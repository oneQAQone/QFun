package me.yxp.qfun.ui.components.molecules

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.core.theme.QFunTheme
import kotlin.math.abs
import kotlin.math.min


private const val LIQUID_GLASS_AGSL = """
    uniform shader content;
    uniform float2 size;
    uniform float2 center;
    uniform float radius;
    uniform float stretchAmount;
    uniform float isDarkMode;

    half4 main(float2 fragCoord) {
        float2 distanceVector = (fragCoord - center) / radius;
        float distanceFromCenter = length(distanceVector);

        float borderMask = smoothstep(1.0, 0.96, distanceFromCenter);
        
        if (distanceFromCenter < 1.0) {
            float surfaceZ = sqrt(1.0 - distanceFromCenter * distanceFromCenter);

            float refractionPower = 0.22 + stretchAmount * 0.55;
            float2 refractionOffset = distanceVector * (1.0 - surfaceZ) * refractionPower * 42.0;

            float dispersionEffect = 0.02 + stretchAmount * 0.08;
            half redChannel = content.eval(fragCoord + refractionOffset * (1.0 + dispersionEffect)).r;
            half greenChannel = content.eval(fragCoord + refractionOffset).g;
            half blueChannel = content.eval(fragCoord + refractionOffset * (1.0 - dispersionEffect)).b;
            half alphaChannel = content.eval(fragCoord + refractionOffset).a;
            
            half4 baseColor = half4(redChannel, greenChannel, blueChannel, alphaChannel);

            float fresnelReflection = pow(1.0 - surfaceZ, 3.5);
            float rimIntensity = isDarkMode > 0.5 ? 0.15 : 0.25;
            baseColor.rgb += fresnelReflection * rimIntensity * borderMask;

            float2 lightDirection = float2(-0.4, -0.4);
            float3 surfaceNormal = float3(distanceVector, surfaceZ);
            float specularHighlight = pow(max(0.0, dot(surfaceNormal, normalize(float3(lightDirection, 1.1)))), 16.0);
            baseColor.rgb += specularHighlight * 0.45 * borderMask;
            
            return baseColor;
        }
        return content.eval(fragCoord);
    }
"""

@Composable
fun FloatingLiquidTabs(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    val hapticFeedback = LocalHapticFeedback.current

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

    val currentSliderHeight = 38.dp + (stretchAmount * 30).dp

    Box(
        modifier = modifier.width(trackWidth).height(80.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(trackWidth).height(trackHeight)
                .clip(CircleShape)
                .shadow(elevation = if (colors.isDark) 0.dp else 4.dp, shape = CircleShape)
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= 31) {
                        renderEffect = RenderEffect.createBlurEffect(120f, 120f, android.graphics.Shader.TileMode.CLAMP).asComposeRenderEffect()
                    }
                }
                .background(
                    color = if (colors.isDark) Color(0xFF18181A).copy(alpha = 0.95f) 
                            else Color.White.copy(alpha = 0.97f),
                    shape = CircleShape
                )
                .border(0.5.dp, Color.White.copy(alpha = 0.08f), CircleShape)
        )

        val liquidShader = remember { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RuntimeShader(LIQUID_GLASS_AGSL) else null }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = currentSliderOffset, y = (stretchAmount * 2).dp)
                .width(currentSliderWidth)
                .height(currentSliderHeight)
                .clip(CircleShape)
                .shadow(
                    elevation = (stretchAmount * 18).dp, 
                    shape = CircleShape,
                    spotColor = (if (colors.isDark) Color.Transparent else Color.Black).copy(alpha = 0.12f)
                )
                .drawWithCache {
                    onDrawWithContent {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && liquidShader != null) {
                            liquidShader.setFloatUniform("size", size.width, size.height)
                            liquidShader.setFloatUniform("center", size.width / 2f, size.height / 2f)
                            liquidShader.setFloatUniform("radius", size.width / 1.7f) 
                            liquidShader.setFloatUniform("stretchAmount", stretchAmount)
                            liquidShader.setFloatUniform("isDarkMode", if (colors.isDark) 1.0f else 0.0f)
                            drawRect(brush = ShaderBrush(liquidShader))
                        }
                        drawContent()
                    }
                }
                .background(
                    color = if (colors.isDark) colors.accentGreen.copy(alpha = 0.22f)
                            else colors.accentBlue.copy(alpha = 0.15f),
                    shape = CircleShape
                )
                .background(
                    brush = Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (colors.isDark) 0.35f else 0.85f),
                        0.30f to Color.Transparent
                    ),
                    shape = CircleShape
                )
                .border(
                    width = (0.8 + stretchAmount * 1.0).dp, 
                    brush = Brush.verticalGradient(listOf(Color.White.copy(0.95f), Color.White.copy(0.15f))),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier.width(trackWidth).height(trackHeight)
        ) {
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

                val textScaleFactor by animateFloatAsState(
                    targetValue = if (isSelected && stretchAmount > 0.05f) 0.96f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "TextParallax"
                )

                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight()
                        .graphicsLayer { 
                            scaleX = textScaleFactor
                            scaleY = textScaleFactor
                        }
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
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.ExtraBold,
                        letterSpacing = (0.5 + stretchAmount * 2.2).sp 
                    )
                }
            }
        }
    }
}