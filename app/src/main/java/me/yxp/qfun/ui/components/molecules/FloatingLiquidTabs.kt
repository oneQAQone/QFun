package me.yxp.qfun.ui.components.molecules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun FloatingLiquidTabs(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors

    Box(
        modifier = modifier.width(220.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidBottomTabs(
            selectedTabIndex = { selectedIndex },
            onTabSelected = onOptionSelected,
            backdrop = backdrop,
            tabsCount = options.size.coerceAtLeast(1)
        ) {
            options.forEachIndexed { index, title ->
                LiquidBottomTab(
                    onClick = { onOptionSelected(index) },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}