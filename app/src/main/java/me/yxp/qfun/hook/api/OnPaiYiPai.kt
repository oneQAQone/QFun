package me.yxp.qfun.hook.api

import com.tencent.qphone.base.remote.FromServiceMsg
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.json.arr
import me.yxp.qfun.utils.json.obj
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.walk
import me.yxp.qfun.utils.qq.QQCurrentEnv
import mqq.app.MSFServlet
import org.json.JSONArray

@HookItemAnnotation("监听拍一拍")
object OnPaiYiPai : BaseApiHookItem<PaiYiPaiListener>() {

    private const val SERVICE_CMD = "trpc.msg.olpush.OlPushService.MsgPush"
    private val QQ_REGEX = Regex("[1-9]\\d{4,12}")

    override fun loadHook() {

        MSFServlet::class.java
            .getDeclaredMethod("onReceive", FromServiceMsg::class.java)
            .hookAfter(this) { param ->

                val fromServiceMsg = param.args[0] as FromServiceMsg
                val cmd = fromServiceMsg.serviceCmd
                if (cmd != SERVICE_CMD) return@hookAfter

                val wupBuffer = fromServiceMsg.wupBuffer

                val data = ProtoData().apply { fromBytes(wupBuffer) }
                val json = data.toJSON()


                val msgHead = json.walk("1", "2").obj ?: return@hookAfter

                val peerUin = json.walk("1", "1", "1").toString()

                val cmd1 = msgHead.optInt("1", 0)
                val cmd2 = msgHead.optInt("2", 0)

                if (json.walk("1", "3") == null) return@hookAfter

                var chatType: Int
                var fromUin: String
                var toUin: String

                when (cmd1) {
                    732 if cmd2 == 20 -> {
                        chatType = 2
                        val content = json.walk("1", "3", "2")?.str ?: return@hookAfter
                        fromUin = extractQQ(content, "1")
                        toUin = extractQQ(content, "2")
                    }

                    528 if cmd2 == 290 -> {
                        chatType = 1
                        fromUin = peerUin
                        val dataArray = json.walk("1", "3", "2", "7")?.arr
                        toUin = extractToUinFromArray(dataArray) ?: return@hookAfter
                    }

                    else -> return@hookAfter
                }

                if (!isValidQQ(fromUin) || toUin != QQCurrentEnv.currentUin) return@hookAfter

                listenerSet.filter { verify(it) }
                    .forEach {
                        it.onPai(peerUin, chatType, fromUin)
                    }
            }
    }

    private fun extractQQ(target: String, type: String): String {
        val key = "uin_str$type"
        val startIndex = target.indexOf(key) + key.length

        val digitStart = target.drop(startIndex).indexOfFirst { it.isDigit() }
        if (digitStart == -1) return ""

        val realStart = startIndex + digitStart
        val realEnd = target.indexOf(':', realStart).takeIf { it != -1 } ?: target.length

        return target.substring(realStart, realEnd)
    }

    private fun extractToUinFromArray(array: JSONArray?): String? {
        if (array == null) return null
        for (i in 0 until array.length()) {
            val item = array[i]?.obj ?: continue
            if (item["1"].str == "uin_str2") {
                return item["2"].str
            }
        }
        return null
    }

    private fun isValidQQ(input: String) = input.matches(QQ_REGEX)
}

fun interface PaiYiPaiListener : Listener {
    fun onPai(peerUin: String, chatType: Int, fromUin: String)
}