package me.yxp.qfun.ui.components.molecules

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedListItem(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val delayMillis = minOf(index * 25, 150)
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        ),
        label = "itemAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        ),
        label = "itemTranslation"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.graphicsLayer {
        this.alpha = alpha; this.translationY = translationY
    }) {
        content()
    }
}
