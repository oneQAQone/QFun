package me.yxp.qfun.hook.msg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.GetMsgRecordListener
import me.yxp.qfun.hook.api.MenuClickListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.ui.components.atoms.DialogTextField
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainer
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts

@HookItemAnnotation(
    "修改本地消息文本",
    "长按消息出现修改文本选项，滑动或重新进入聊天界面刷新",
    HookCategory.MSG
)
object ModifyMsgContent : BaseSwitchHookItem(), GetMsgRecordListener, MenuClickListener {

    override val menuKey: String = "[QFun],$name,文本修改,,2,9"

    private val msgMap = LinkedHashMap<Long, Map<Int, String>>()

    override fun onGet(msgRecord: MsgRecord) {

        val items = msgMap[msgRecord.msgId] ?: return
        val elements = msgRecord.elements

        items.forEach { (index, newContent) ->
            if (index in elements.indices) {
                val element = elements[index]
                element.textElement?.let {
                    it.content = newContent
                }
            }
        }
    }

    override fun onClick(msgData: MsgData) {
        val msgRecord = msgData.data
        val items = LinkedHashMap<Int, String>()

        try {
            val elements = msgRecord.elements

            elements.forEachIndexed { index, element ->
                val textElement = element.textElement ?: return@forEachIndexed
                if (textElement.atType == 0) {
                    items[index] = textElement.content
                }
            }

            if (items.isNotEmpty()) {
                showEditDialog(msgRecord.msgId, items)
            }
        } catch (t: Throwable) {
            LogUtils.e(this, t)
        }
    }

    private fun showEditDialog(msgId: Long, items: Map<Int, String>) {
        val activity = QQCurrentEnv.activity ?: return

        QFunCenterDialog(activity) { dismiss ->
            val editValues =
                remember { mutableStateListOf<String>().apply { addAll(items.values) } }
            val indexList = items.keys.toList()

            CenterDialogContainer("修改本地文本", dismiss, {
                dismiss()
                val modifications = LinkedHashMap<Int, String>()
                editValues.forEachIndexed { i, newText ->
                    val originalText = items[indexList[i]]
                    if (newText != originalText) modifications[indexList[i]] = newText
                }
                if (modifications.isNotEmpty()) {
                    val currentMods = msgMap[msgId]?.toMutableMap() ?: LinkedHashMap()
                    currentMods.putAll(modifications)
                    msgMap[msgId] = currentMods
                }
                Toasts.qqToast(2, "滑动或重进刷新")
            }) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(items.values.toList()) { index, originalContent ->
                        DialogTextField(
                            value = editValues[index],
                            onValueChange = { editValues[index] = it },
                            label = "文本 ${index + 1}",
                            hint = "原文本: $originalContent"
                        )
                    }
                }
            }
        }.show()
    }
}