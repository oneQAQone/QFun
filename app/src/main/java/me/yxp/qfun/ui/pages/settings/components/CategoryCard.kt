package me.yxp.qfun.ui.pages.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun CategoryCard(
    name: String,
    totalCount: Int,
    enabledCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors

    QFunCard(modifier = modifier.fillMaxWidth(), animateContentSize = false, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "$totalCount 个功能 · 已开启 $enabledCount 个",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
            Text("›", fontSize = 28.sp, color = colors.textSecondary.copy(0.4f))
        }
    }
}
