package me.yxp.qfun.ui.pages.configs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.yxp.qfun.conf.EmoticonExportConfig
import me.yxp.qfun.hook.file.EmoticonExport
import me.yxp.qfun.ui.components.listitems.ActionItem
import me.yxp.qfun.ui.components.listitems.PreferenceSection
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold
import me.yxp.qfun.ui.core.theme.AccentBlue
import me.yxp.qfun.ui.core.theme.QFunTheme

private const val COLUMNS = 4

@Composable
fun EmoticonExportPage(
    currentConfig: EmoticonExportConfig,
    onSave: (EmoticonExportConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember(currentConfig) { mutableStateOf(currentConfig.selected) }
    var emoticons by remember { mutableStateOf<List<Any>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(reloadKey) {
        loading = true
        emoticons = withContext(Dispatchers.IO) { EmoticonExport.loadEmoticons() }
        loading = false
    }

    fun toggle(id: String) {
        val newSet = selected.toMutableSet()
        if (!newSet.remove(id)) newSet.add(id)
        selected = newSet
    }

    ConfigPageScaffold(
        title = "导出收藏表情",
        configData = EmoticonExportConfig(selected),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        PreferenceSection(title = "导出") {
            ActionItem(
                title = if (loading) "正在读取收藏表情…" else "一键下载全部（${emoticons.size} 个）",
                description = "导出到 ${EmoticonExport.exportDir}",
                onClick = { EmoticonExport.export(emoticons) }
            )
            if (selected.isNotEmpty()) {
                ActionItem(
                    title = "下载选中的 ${selected.size} 个",
                    description = "只导出下面勾选的",
                    onClick = {
                        EmoticonExport.export(emoticons.filter { EmoticonExport.idOf(it) in selected })
                    }
                )
            }
            ActionItem(
                title = if (loading) "读取中…" else "刷新列表（${emoticons.size} 个）",
                description = "收藏夹变动后重新读取",
                onClick = { reloadKey++ }
            )
        }

        PreferenceSection(title = "收藏表情（点图选择，已选 ${selected.size} 个）") {
            when {
                loading -> Hint("读取中…")

                emoticons.isEmpty() -> Hint("没读到收藏表情，确认已登录 QQ 且收藏夹里有自定义表情")

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(COLUMNS),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(emoticons, key = { EmoticonExport.idOf(it) }) { item ->
                        val id = EmoticonExport.idOf(item)
                        EmoticonCell(
                            item = item,
                            isSelected = selected.contains(id),
                            onClick = { toggle(id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    val colors = QFunTheme.colors
    Text(
        text = text,
        fontSize = 13.sp,
        color = colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun EmoticonCell(
    item: Any,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val colors = QFunTheme.colors

    val bitmap by produceState<ImageBitmap?>(initialValue = null, item) {
        value = EmoticonExport.thumbnail(context, item)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBackground)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentBlue else colors.textSecondary.copy(0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "…",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }
}
