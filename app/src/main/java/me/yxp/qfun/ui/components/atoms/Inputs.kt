package me.yxp.qfun.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun QFunTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = QFunTheme.colors
    val options = keyboardOptions ?: KeyboardOptions(keyboardType = keyboardType)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 15.sp, color = colors.textPrimary, lineHeight = 20.sp),
        cursorBrush = SolidColor(AccentBlue),
        singleLine = singleLine,
        keyboardOptions = options,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.cardBackground)
                    .border(1.dp, colors.textSecondary.copy(0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = Dimens.PaddingMedium),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && hint.isNotEmpty()) {
                    Text(hint, fontSize = 15.sp, color = colors.textSecondary, lineHeight = 20.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun DialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardOptions: KeyboardOptions? = null,
    singleLine: Boolean = true
) {
    val colors = QFunTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
        }
        QFunTextField(
            value,
            onValueChange,
            hint = hint,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardType = keyboardType
        )
    }
}
