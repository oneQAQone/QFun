package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.AdConfig
import me.yxp.qfun.ui.components.listitems.PreferenceSection
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

private val AD_SLOTS = listOf(
    Triple(
        AdConfig.QZONE_FEED,
        "QQ空间信息流广告",
        "空间/好友动态里插入的广告条目"
    ),
    Triple(
        AdConfig.GDT_NATIVE,
        "原生广告（GDT 总闸）",
        "拦截广告请求，其他页面里的信息流广告一并受影响"
    ),
    Triple(
        AdConfig.GDT_BANNER,
        "横幅广告",
        "各处横幅广告位"
    ),
    Triple(
        AdConfig.LOADING_AD,
        "开屏 / 加载页广告",
        "启动与加载时展示的广告"
    ),
    Triple(
        AdConfig.LEBA_SHOPPING,
        "动态购物红点广告",
        "动态页购物入口的广告红点"
    ),
    Triple(
        AdConfig.GDT_PRELOAD,
        "广告预加载",
        "提前拉取并缓存广告，关掉可减少广告相关流量"
    )
)

@Composable
fun AdBlockPage(
    currentConfig: AdConfig,
    onSave: (AdConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var blocked by remember(currentConfig) { mutableStateOf(currentConfig.blocked) }

    fun toggle(key: String) {
        val newSet = blocked.toMutableSet()
        if (!newSet.remove(key)) newSet.add(key)
        blocked = newSet
    }

    ConfigPageScaffold(
        title = "广告净化",
        configData = AdConfig(blocked),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        PreferenceSection(title = "勾选 = 屏蔽该位置的广告") {
            SelectionGroup {
                AD_SLOTS.forEach { (key, label, desc) ->
                    SelectionItem(
                        title = label,
                        subtitle = desc,
                        isSelected = blocked.contains(key),
                        onClick = { toggle(key) }
                    )
                }
            }
        }
    }
}
