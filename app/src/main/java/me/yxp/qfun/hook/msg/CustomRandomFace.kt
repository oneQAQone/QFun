package me.yxp.qfun.hook.msg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tencent.qphone.base.remote.ToServiceMsg
import com.tencent.qqnt.kernel.api.impl.MsgService
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainerNoButton
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.reflect.findMethod
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.Random
import kotlin.math.abs

@HookItemAnnotation(
    "自定义随机表情",
    "发送猜拳/骰子时显示选择框，可自定义点数结果",
    HookCategory.MSG
)
object CustomRandomFace : BaseSwitchHookItem() {

    private lateinit var sendMsg: Method

    override fun onInit(): Boolean {

        sendMsg = MsgService::class.java
            .findMethod {
                name = "sendMsg"
                paramCount = 5
            }
        return super.onInit()

    }

    @Suppress("UNCHECKED_CAST")
    override fun onHook() {
        sendMsg.hookReplace(this) { param ->

            val contact = param.args[1] as Contact
            val chatType = contact.chatType
            val elements = param.args[2] as ArrayList<MsgElement>

            if (chatType == 1 || chatType == 2) {
                val element = elements.firstOrNull() ?: return@hookReplace param.invokeOriginal()
                if (element.elementType == 6) {
                    val faceText = element.faceElement.faceText
                    val (title, values) = when (faceText) {
                        "/骰子" -> "自定义骰子" to arrayOf("1点", "2点", "3点", "4点", "5点", "6点")
                        "/包剪锤" -> "自定义猜拳" to arrayOf("布", "剪刀", "石头")
                        else -> return@hookReplace param.invokeOriginal()
                    }
                    showSelectionDialog(title, values, faceText, contact.peerUid, chatType)
                    return@hookReplace null
                }
            }

            return@hookReplace param.invokeOriginal()

        }
    }

    private fun showSelectionDialog(
        title: String,
        values: Array<String>,
        faceText: String,
        peerUid: String,
        chatType: Int
    ) {
        ModuleScope.launchMain {
            val activity = QQCurrentEnv.activity ?: return@launchMain

            QFunCenterDialog(activity) { dismiss ->
                CenterDialogContainerNoButton(title) {
                    SelectionList(values.toList()) { index ->
                        Toasts.qqToast(2, "已发送: ${values[index]}")
                        dismiss()
                        try {
                            val bytes = makeBytes(faceText, index + 1, peerUid, chatType)
                            if (bytes.isNotEmpty()) sendBuffer(bytes)
                            ModuleScope.launchDelayed(1000) {
                                QQCurrentEnv.kernelMsgService?.startMsgSync()
                            }
                        } catch (e: Exception) {
                            LogUtils.e(this@CustomRandomFace, e)
                        }

                    }
                }
            }.show()
        }
    }

    private fun sendBuffer(bytes: ByteArray) {


        val toServiceMsg = ToServiceMsg(
            "mobileqq.service",
            QQCurrentEnv.currentUin,
            "MessageSvc.PbSendMsg"
        )


        toServiceMsg.putWupBuffer(bytes)


        toServiceMsg.addAttribute("req_pb_protocol_flag", true)

        QQCurrentEnv.qQAppInterface.sendToService(toServiceMsg)

    }

    private fun makeBytes(type: String, value: Int, peer: String, chatType: Int): ByteArray {
        val isDice = type == "/骰子"

        val formattedPeer = if (chatType == 1) "\"$peer\"" else peer

        val v1 = if (isDice) "33" else "34"
        val v2 = if (isDice) "358" else "359"

        val random1 = abs(Random().nextInt())
        val random2 = abs(Random().nextInt())

        val jsonStr = """
            {
                "1": {
                    "$chatType": {
                        "${3 - chatType}": $formattedPeer
                    }
                },
                "3": {
                    "1": {
                        "2": {
                            "53": {
                                "1": 37,
                                "2": {
                                    "1": "1",
                                    "2": "$v1",
                                    "3": $v2,
                                    "4": 1,
                                    "5": 2,
                                    "6": "$value",
                                    "7": "$type",
                                    "9": 1
                                },
                                "3": 20
                            }
                        }
                    }
                },
                "4": $random1,
                "5": $random2
            }
        """.trimIndent()

        return try {
            val json = JSONObject(jsonStr)
            val data = ProtoData()
            data.fromJSON(json)
            data.toBytes()
        } catch (e: Exception) {
            LogUtils.e(this, e)
            ByteArray(0)
        }
    }
}

@Composable
private fun SelectionList(items: List<String>, onItemClick: (Int) -> Unit) {
    val colors = QFunTheme.colors
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
    ) {
        itemsIndexed(items) { index, item ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.background)
                    .clickable { onItemClick(index) }
                    .padding(20.dp, 16.dp),
                Alignment.Center
            ) {
                Text(item, fontSize = 16.sp, color = colors.textPrimary)
            }
        }
    }
}