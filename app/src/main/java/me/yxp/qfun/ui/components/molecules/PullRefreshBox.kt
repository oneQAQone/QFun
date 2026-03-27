package me.yxp.qfun.ui.components.molecules

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.yxp.qfun.ui.components.atoms.LoadingIndicator
import me.yxp.qfun.ui.core.theme.QFunTheme
import kotlin.math.roundToInt

@Composable
fun PullRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val maxOffsetPx = with(density) { 150.dp.toPx() }
    val thresholdPx = with(density) { 65.dp.toPx() }
    val indicatorSizePx = with(density) { 42.dp.toPx() }

    val offsetY = remember { Animatable(0f) }

    var touchDownY by remember { mutableStateOf(0f) }
    var boxHeight by remember { mutableStateOf(0f) }
    var hasScrolledThisDrag by remember { mutableStateOf(false) }

    val currentIsRefreshing by rememberUpdatedState(isRefreshing)
    val currentOnRefresh by rememberUpdatedState(onRefresh)

    LaunchedEffect(currentIsRefreshing) {
        if (currentIsRefreshing) {
            if (offsetY.value < thresholdPx) {
                offsetY.animateTo(thresholdPx, tween(250))
            }
            delay(10000)
            if (offsetY.value > 0f) {
                offsetY.animateTo(0f, tween(300))
            }
        } else {
            offsetY.animateTo(0f, tween(300))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch { offsetY.snapTo(0f) }
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentIsRefreshing) return Offset.Zero

                if (source == NestedScrollSource.Drag) {
                    if (available.y < 0 && offsetY.value == 0f) {
                        hasScrolledThisDrag = true
                    }
                }

                if (available.y < 0 && offsetY.value > 0) {
                    val current = offsetY.value
                    val newOffset = (current + available.y).coerceAtLeast(0f)
                    val consumed = newOffset - current
                    scope.launch { offsetY.snapTo(newOffset) }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (currentIsRefreshing) return Offset.Zero

                if (source == NestedScrollSource.Drag) {
                    if (consumed.y > 0) {
                        hasScrolledThisDrag = true
                    }

                    val allowPullForThisDrag = !hasScrolledThisDrag && (boxHeight == 0f || touchDownY <= boxHeight / 2f)

                    if (allowPullForThisDrag && available.y > 0) {
                        val current = offsetY.value
                        val newOffset = (current + available.y).coerceAtMost(maxOffsetPx)
                        val consumedY = newOffset - current
                        scope.launch { offsetY.snapTo(newOffset) }
                        return Offset(0f, consumedY)
                    }
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                hasScrolledThisDrag = false

                if (currentIsRefreshing) return Velocity.Zero
                
                if (offsetY.value > 0) {
                    if (offsetY.value >= thresholdPx) {
                        currentOnRefresh()
                        offsetY.animateTo(thresholdPx, tween(200))
                    } else {
                        offsetY.animateTo(0f, tween(250))
                    }
                    return Velocity(0f, available.y)
                }
                return Velocity.Zero
            }
        }
    }

    val dummyScrollableState = rememberScrollableState { 0f }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { boxHeight = it.size.height.toFloat() }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val downChange = event.changes.firstOrNull { it.pressed && !it.previousPressed }
                        if (downChange != null) {
                            touchDownY = downChange.position.y
                        }
                    }
                }
            }
            .nestedScroll(nestedScrollConnection)
    ) {
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    state = dummyScrollableState,
                    orientation = Orientation.Vertical
                )
        ) {
            content()
        }

        val currentOffset = offsetY.value
        if (currentOffset > 0f) {
            val pullRatio = (currentOffset / thresholdPx).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(0, (currentOffset - indicatorSizePx).roundToInt()) }
                    .alpha(pullRatio)
                    .shadow(elevation = 4.dp, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(QFunTheme.colors.cardBackground)
                    .size(42.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentIsRefreshing) {
                    LoadingIndicator()
                } else {
                    CircularProgressIndicator(
                        progress = { pullRatio },
                        modifier = Modifier.fillMaxSize(),
                        color = QFunTheme.colors.accentGreen,
                        strokeWidth = 2.5.dp,
                        trackColor = QFunTheme.colors.accentGreen.copy(alpha = 0.18f)
                    )
                }
            }
        }
    }
}