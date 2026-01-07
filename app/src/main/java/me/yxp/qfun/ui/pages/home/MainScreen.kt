package me.yxp.qfun.ui.pages.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.R
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.core.theme.AccentGreen
import me.yxp.qfun.ui.core.theme.AccentRed
import me.yxp.qfun.ui.core.theme.QFunTheme

@Composable
fun MainScreen(
    versionName: String,
    versionCode: Int,
    isActivated: Boolean,
    frameworkInfo: String,
    isIconVisible: Boolean,
    onToggleIcon: () -> Unit,
    onTelegramClick: () -> Unit,
    onGithubClick: () -> Unit,
    onQQGroupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QFunTheme.colors
    val iconButtonAlpha by animateFloatAsState(
        targetValue = if (isIconVisible) 1f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "iconAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "QFun",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("$versionName($versionCode)", fontSize = 14.sp, color = colors.textSecondary)
            }
            QFunCard(
                modifier = Modifier.alpha(iconButtonAlpha),
                animateContentSize = false,
                onClick = onToggleIcon
            ) {
                Text(
                    if (isIconVisible) "隐藏图标" else "显示图标",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(16.dp, 10.dp)
                )
            }
        }

        QFunCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(if (isActivated) R.drawable.ic_logo_check else R.drawable.ic_logo_unchecked),
                    null,
                    Modifier.size(52.dp),
                    if (isActivated) AccentGreen else AccentRed
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        if (isActivated) "已激活" else "未激活",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        frameworkInfo,
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinkCard(
                R.drawable.ic_logo_telegram,
                "Telegram Channel",
                "获取最新更新和公告",
                onTelegramClick
            )
            LinkCard(
                R.drawable.ic_logo_github,
                "Github Repository",
                "查看源码和提交问题",
                onGithubClick
            )
            LinkCard(R.drawable.ic_logo_qq, "QQ交流群", "加入社区讨论", onQQGroupClick)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun LinkCard(iconRes: Int, title: String, subtitle: String, onClick: () -> Unit) {
    val colors = QFunTheme.colors

    QFunCard(modifier = Modifier.fillMaxWidth(), animateContentSize = false, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(iconRes), title, Modifier.size(36.dp), colors.textPrimary)
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 13.sp, color = colors.textSecondary)
            }
        }
    }
}
