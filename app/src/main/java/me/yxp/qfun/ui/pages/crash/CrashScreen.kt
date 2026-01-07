package me.yxp.qfun.ui.pages.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.yxp.qfun.ui.components.atoms.DialogButton
import me.yxp.qfun.ui.components.atoms.QFunCard
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.core.theme.AccentRed
import me.yxp.qfun.ui.core.theme.QFunTheme
import java.io.File

@Composable
fun CrashScreen(
    hostName: String,
    blamedModule: String,
    exceptionType: String,
    summary: String,
    reportPath: String,
    stackTrace: String,
    onRestart: () -> Unit,
) {
    val colors = QFunTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { },
        contentAlignment = Alignment.Center
    ) {
        CrashContent(
            hostName = hostName,
            blamedModule = blamedModule,
            exceptionType = exceptionType,
            summary = summary,
            reportPath = reportPath,
            stackTrace = stackTrace,
            onRestart = onRestart,
        )
    }
}

@Composable
private fun CrashContent(
    hostName: String,
    blamedModule: String,
    exceptionType: String,
    summary: String,
    reportPath: String,
    stackTrace: String,
    onRestart: () -> Unit,
) {
    val colors = QFunTheme.colors
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    BaseDialogSurface(
        title = "$hostName 停止运行",
        bottomBar = {
            Spacer(modifier = Modifier.width(12.dp))
            DialogButton("重启应用", onRestart, isPrimary = true)
        }
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Text(
                text = exceptionType,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentRed
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = summary,
                fontSize = 14.sp,
                color = colors.textPrimary,
                maxLines = 4,
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "错误来源: ",
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = blamedModule,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (blamedModule.contains("QFun")) AccentRed else colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.textSecondary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            QFunCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Log Path", reportPath)
                        clipboard.setPrimaryClip(clip)
                        Toast
                            .makeText(context, "日志路径已复制", Toast.LENGTH_SHORT)
                            .show()
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "日志路径 (点击复制)",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ".../${File(reportPath).name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = !isExpanded }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "详细堆栈信息",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = if (isExpanded) "收起" else "展开",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.background)
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = stackTrace,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}