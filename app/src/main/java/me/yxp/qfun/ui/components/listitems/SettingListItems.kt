package me.yxp.qfun.ui.components.listitems

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.atoms.QFunSwitch
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun CommonActionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: Painter? = null,
    isAvailable: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val alpha by animateFloatAsState(
        targetValue = if (isAvailable) 1f else 0.5f,
        animationSpec = tween(durationMillis = 200),
        label = "cardAlpha"
    )

    QFunCard(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        onClick = if (isAvailable) onClick else null
    ) {
        BaseListItem(
            title = title,
            subtitle = subtitle,
            leadingIcon = leadingIcon,
            trailingContent = trailingContent,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
fun SwitchActionCard(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: Painter? = null,
    isAvailable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val displaySubtitle = when {
        !isAvailable && subtitle.isNullOrEmpty() -> "当前环境不可用"
        !isAvailable -> "$subtitle (当前环境不可用)"
        else -> subtitle
    }

    CommonActionCard(
        title = title,
        modifier = modifier,
        subtitle = displaySubtitle,
        leadingIcon = leadingIcon,
        isAvailable = isAvailable,
        onClick = onClick ?: { if (isAvailable) onCheckedChange(!isChecked) },
        trailingContent = {
            QFunSwitch(isChecked, { if (isAvailable) onCheckedChange(it) }, enabled = isAvailable)
        }
    )
}

@Composable
fun SwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: Painter? = null,
    enabled: Boolean = true
) {
    BaseListItem(
        title = title,
        modifier = modifier,
        subtitle = description,
        leadingIcon = icon,
        trailingContent = { QFunSwitch(checked, onCheckedChange, enabled = enabled) },
        onClick = { onCheckedChange(!checked) },
        enabled = enabled
    )
}

@Composable
fun ActionItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: Painter? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val colors = QFunTheme.colors
    BaseListItem(
        title = title,
        modifier = modifier,
        subtitle = description,
        leadingIcon = icon,
        trailingContent = trailingContent ?: {
            Text(
                "›",
                fontSize = 20.sp,
                color = colors.textSecondary
            )
        },
        onClick = onClick
    )
}

@Composable
fun SelectionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AccentBlue else colors.textPrimary,
        animationSpec = tween(durationMillis = 200),
        label = "selectionTextColor"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = colors.textSecondary)
        }
        if (isSelected) {
            Text("✓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
        }
    }
}

@Composable
fun InputItem(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onDone: (() -> Unit)? = null
) {
    val colors = QFunTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.cardBackground)
                .border(1.dp, colors.textSecondary.copy(0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = colors.textPrimary,
                lineHeight = 20.sp
            ),
            singleLine = singleLine,
            cursorBrush = SolidColor(AccentBlue),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = if (onDone != null) ImeAction.Done else ImeAction.Default
            ),
            keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = colors.textSecondary,
                            lineHeight = 20.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}


@Composable
fun SelectionGroup(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), content = { content() })
}

@Composable
fun PreferenceSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = QFunTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        content()
    }
}
