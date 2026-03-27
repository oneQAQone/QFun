package me.yxp.qfun.ui.components.molecules

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
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

    val thresholdPx = with(density) { 65.dp.toPx() }
    val maxOffsetPx = with(density) { 150.dp.toPx() }

    val logic = remember {
        PullRefreshLogic(
            scope = scope,
            onRefresh = onRefresh,
            threshold = thresholdPx,
            maxOffset = maxOffsetPx
        )
    }

    LaunchedEffect(isRefreshing) {
        logic.updateRefreshingState(isRefreshing)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(logic.connection)
    ) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset {
                    IntOffset(0, (logic.offsetY.value - 48.dp.toPx()).roundToInt())
                }
                .alpha((logic.offsetY.value / thresholdPx).coerceIn(0f, 1f))
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRefreshing) {
                LoadingIndicator()
            } else {
                CircularProgressIndicator(
                    progress = { (logic.offsetY.value / thresholdPx).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = QFunTheme.colors.accentGreen,
                    strokeWidth = 3.dp,
                    trackColor = QFunTheme.colors.accentGreen.copy(alpha = 0.1f)
                )
            }
        }


    }
}

private class PullRefreshLogic(
    private val scope: CoroutineScope,
    private val onRefresh: () -> Unit,
    private val threshold: Float,
    private val maxOffset: Float
) {

    val offsetY = Animatable(0f)
    private var isRefreshingState = false

    fun updateRefreshingState(refreshing: Boolean) {
        isRefreshingState = refreshing
        if (!refreshing && offsetY.value > 0) {
            scope.launch { offsetY.animateTo(0f) }
        }
    }

    val connection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return if (source == NestedScrollSource.UserInput && available.y < 0 && offsetY.value > 0) {
                val newOffset = (offsetY.value + available.y).coerceAtLeast(0f)
                val consumed = newOffset - offsetY.value
                scope.launch { offsetY.snapTo(newOffset) }
                Offset(0f, consumed)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.UserInput && available.y > 0 && !isRefreshingState) {

                val resistance = 0.5f
                val newOffset = (offsetY.value + available.y * resistance).coerceAtMost(maxOffset)
                scope.launch { offsetY.snapTo(newOffset) }
                Offset(0f, available.y)
            } else {
                Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val hadOffset = offsetY.value > 0
            if (offsetY.value >= threshold) {
                onRefresh()
                offsetY.animateTo(threshold)
            } else if (offsetY.value > 0) {
                offsetY.animateTo(0f)
            }
            return if (hadOffset) available else Velocity.Zero
        }
    }
}