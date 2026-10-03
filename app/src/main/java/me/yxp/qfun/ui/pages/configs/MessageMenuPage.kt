package me.yxp.qfun.ui.pages.configs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.yxp.qfun.conf.MenuConfig
import me.yxp.qfun.ui.components.listitems.SelectionGroup
import me.yxp.qfun.ui.components.listitems.SelectionItem
import me.yxp.qfun.ui.components.scaffold.ConfigPageScaffold

private val MENU_ENTRIES = listOf(
    "CopyMenuItem" to "复制",
    "CopyTextMenuItem" to "复制文本",
    "MarkdownBubbleCopyMenuItem" to "复制（Markdown）",
    "ReplyMenuItem" to "引用",
    "ForwardMenuItem" to "转发",
    "ForwardTextMenuItem" to "转发文本",
    "ForwardGroupAlbumMenuItem" to "转发到群相册",
    "FavMenuItem" to "收藏",
    "DelMenuItem" to "删除",
    "RevokeMenuItem" to "撤回",
    "MultiSelectMenuItem" to "多选",
    "MultiMenuItem" to "多选（旧）",
    "PackUpMenuItem" to "收起",
    "SelectAllMenuItem" to "全选",
    "PartialSelectMenuItem" to "部分选择",
    "EditMenuItem" to "编辑",
    "AddExpressionMenuItem" to "添加表情",
    "SaveToEmojiMenuItem" to "保存到表情",
    "RelatedEmotionMenuItem" to "查找相关表情",
    "EmotionMenuItem" to "表情",
    "AddOneMenuItem" to "+1",
    "SendSameMenuItem" to "复读",
    "MakeSameMenuItem" to "跟读",
    "ShootSameMenuItem" to "跟拍",
    "MsgInfoMenuItem" to "消息信息",
    "JumpToSourceMenuItem" to "跳转到来源",
    "AddEssenceMenuItem" to "加精华",
    "ScreenShotMenuItem" to "截图",
    "SaveToLocalMenuItem" to "保存到本地",
    "SaveWeiYunMenuItem" to "保存到微云",
    "SendToPcMenuItem" to "发送到电脑",
    "TranslateMenuItem" to "翻译",
    "ClickTtsMenuItem" to "朗读",
    "PttSpeechToTextMenuItem" to "语音转文字",
    "PttSpeakerPhoneMenuItem" to "扬声器播放",
    "MutePlayMenuItem" to "静音播放",
    "PttSpeedModeMenuItem" to "倍速播放",
    "SearchCopyTextMenuItem" to "搜索复制文本",
    "NetSearchCopyMenuItem" to "联网搜索复制",
    "NetSearchFavMenuItem" to "联网搜索收藏",
    "NetSearchForwardMenuItem" to "联网搜索转发",
    "NetSearchSelectTextMenuItem" to "联网搜索选词",
    "NetSearchAskBabyQMenuItem" to "联网搜索问Q宝",
    "RobotLikeMenuItem" to "机器人点赞",
    "RobotDislikeMenuItem" to "机器人点踩",
    "RobotReportMenuItem" to "机器人举报",
    "AskQBaoMenuItem" to "问Q宝",
    "YuanbaoAiMenuItem" to "元宝AI",
    "TroopTodoMenuItem" to "群待办",
    "TroopDragonLadderMenuItem" to "群接龙",
    "UpcomingMenuItem" to "待办",
    "JumpDressUpMenuItem" to "跳转装扮",
    "AreaSelectMenuItem" to "区域选择"
)

@Composable
fun MessageMenuPage(
    currentConfig: MenuConfig,
    onSave: (MenuConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var hidden by remember(currentConfig) { mutableStateOf(currentConfig.hiddenKeys) }

    fun toggle(key: String) {
        val newSet = hidden.toMutableSet()
        if (!newSet.remove(key)) newSet.add(key)
        hidden = newSet
    }

    ConfigPageScaffold(
        title = "长按菜单精简",
        configData = MenuConfig(hidden),
        onSave = onSave,
        onDismiss = onDismiss
    ) {
        SelectionGroup {
            MENU_ENTRIES.forEach { (key, label) ->
                SelectionItem(
                    title = label,
                    subtitle = key,
                    isSelected = !hidden.contains(key),
                    onClick = { toggle(key) }
                )
            }
        }
    }
}
