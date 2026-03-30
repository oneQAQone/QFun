package me.yxp.qfun.ui.components.molecules

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun QFunTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    themeMode: Int = 0,
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    val colors = QFunTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            QFunCard(
                modifier = Modifier.size(40.dp),
                animateContentSize = false,
                onClick = onBackClick
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 22.sp, color = colors.textPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = title,
            fontSize = if (showBackButton) 20.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colors.cardBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = colors.ripple),
                        onClick = onThemeToggle
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when(themeMode) {
                    0 -> if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                    else -> R.drawable.ic_theme_auto
                }
                val themeText = when(themeMode) {
                    0 -> if (isDarkTheme) "浅色" else "深色"
                    else -> "跟随"
                }

                AnimatedContent(
                    targetState = iconRes,
                    transitionSpec = {
                        scaleIn(tween(200)) + fadeIn(tween(200)) togetherWith
                                scaleOut(tween(200)) + fadeOut(tween(200))
                    },
                    label = "themeIcon"
                ) { targetIcon ->
                    Icon(
                        painterResource(targetIcon),
                        contentDescription = "Toggle theme",
                        Modifier.size(18.dp),
                        colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = themeText,
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun TopBarCapsuleButton(iconRes: Int, label: String, onClick: () -> Unit) {
    val colors = QFunTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(colors.cardBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.ripple),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(iconRes),
            contentDescription = label,
            Modifier.size(18.dp),
            colors.textPrimary
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}