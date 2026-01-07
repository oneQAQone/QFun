package me.yxp.qfun.ui.components.atoms

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun QFunCard(
    modifier: Modifier = Modifier,
    animateContentSize: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(Dimens.CardCornerRadius)
    val colors = QFunTheme.colors

    Box(
        modifier = modifier
            .shadow(
                elevation = if (colors.isDark) 0.dp else 2.dp,
                shape = shape,
                ambientColor = colors.textSecondary.copy(alpha = 0.08f),
                spotColor = colors.textSecondary.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(colors.cardBackground)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = colors.ripple),
                    onClick = onClick
                ) else Modifier
            )
            .then(
                if (animateContentSize) Modifier
                    .heightIn(min = 40.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = 0.5f,
                            stiffness = 400f,
                            visibilityThreshold = IntSize(2, 2)
                        )
                    )
                else Modifier
            ),
        content = content
    )
}