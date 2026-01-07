package me.yxp.qfun.hook.api

import com.tencent.qphone.base.remote.FromServiceMsg
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.json.long
import me.yxp.qfun.utils.json.num
import me.yxp.qfun.utils.json.obj
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.walk
import me.yxp.qfun.utils.qq.FriendTool
import mqq.app.MSFServlet

@HookItemAnnotation("监听群禁言")
object OnTroopShutUp : BaseApiHookItem<TroopShutUpListener>() {

    private const val SERVICE_CMD = "trpc.msg.olpush.OlPushService.MsgPush"

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

                val msgHead = json.walk("1", "2")?.obj ?: return@hookAfter
                if (msgHead["1"].num != 732 || msgHead["2"].num != 12) {
                    return@hookAfter
                }

                val groupInfo = json.walk("1", "3", "2")?.obj ?: return@hookAfter

                val troopUin = groupInfo["1"].long?.toString() ?: return@hookAfter
                val opUid = groupInfo["4"].str ?: ""
                val opUin = FriendTool.getUinFromUid(opUid)

                val shutUpInfo = groupInfo.walk("5", "3")?.obj ?: return@hookAfter

                val memberUid = shutUpInfo["1"].str ?: ""
                val memberUin = FriendTool.getUinFromUid(memberUid)
                val time = shutUpInfo["2"].long ?: 0L

                listenerSet.filter { verify(it) }
                    .forEach {
                        it.onShutUp(troopUin, memberUin, time, opUin)
                    }
            }
    }
}

fun interface TroopShutUpListener : Listener {
    fun onShutUp(troopUin: String, memberUin: String, time: Long, opUin: String)
}