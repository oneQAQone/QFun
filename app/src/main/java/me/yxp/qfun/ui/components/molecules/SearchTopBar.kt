package me.yxp.qfun.ui.components.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.R
import me.yxp.qfun.ui.core.theme.QFunTheme

data class TopBarMenuItem(
    val title: String,
    val iconRes: Int,
    val onClick: () -> Unit
)

@Composable
fun SearchTopBar(
    title: String,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    themeMode: Int,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    menuItems: List<TopBarMenuItem> = emptyList(),
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    searchHint: String = "搜索..."
) {
    val colors = QFunTheme.colors
    var showMenu by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val silkEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {

        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(tween(300, delayMillis = 100)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.95f)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 22.sp, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(onClick = onThemeToggle) {
                        val iconRes = when (themeMode) {
                            0 -> if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                            else -> R.drawable.ic_theme_auto
                        }
                        Icon(
                            painterResource(iconRes),
                            null,
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = { onSearchActiveChange(true) }) {
                        Icon(
                            painterResource(R.drawable.ic_search),
                            null,
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (menuItems.isNotEmpty()) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    painterResource(R.drawable.ic_more_vert),
                                    null,
                                    tint = colors.textPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            MaterialTheme(
                                shapes = MaterialTheme.shapes.copy(
                                    extraSmall = RoundedCornerShape(
                                        24.dp
                                    )
                                )
                            ) {
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier
                                        .width(190.dp)
                                        .background(colors.cardBackground.copy(alpha = 0.85f))
                                        .border(
                                            0.5.dp,
                                            colors.textPrimary.copy(alpha = 0.1f),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(vertical = 8.dp),
                                    offset = DpOffset(0.dp, 8.dp)
                                ) {
                                    menuItems.forEach { item ->
                                        DropdownMenuItem(
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 12.dp
                                            ),
                                            text = {
                                                Text(
                                                    text = item.title,
                                                    color = colors.textPrimary,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            leadingIcon = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(CircleShape)
                                                        .background(colors.accentBlue.copy(alpha = 0.1f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(item.iconRes),
                                                        contentDescription = null,
                                                        tint = colors.accentBlue,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                item.onClick()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInHorizontally(
                initialOffsetX = { it / 2 },
                animationSpec = tween(400, easing = silkEasing)
            ) + fadeIn() + scaleIn(
                initialScale = 0.8f,
                transformOrigin = TransformOrigin(1f, 0.5f)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = silkEasing)
            ) + fadeOut() + scaleOut(
                targetScale = 0.8f,
                transformOrigin = TransformOrigin(1f, 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(0.1f))
                    .clip(CircleShape)
                    .background(colors.cardBackground)
                    .border(0.5.dp, colors.accentBlue.copy(0.2f), CircleShape)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = colors.accentBlue,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(colors.accentBlue),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    searchHint,
                                    color = colors.textSecondary.copy(0.7f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                IconButton(
                    onClick = {
                        if (searchQuery.isNotEmpty()) {
                            onQueryChange("")
                        } else {
                            onSearchActiveChange(false)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}