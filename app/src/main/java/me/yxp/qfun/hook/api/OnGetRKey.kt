package me.yxp.qfun.hook.api

import com.tencent.mobileqq.msf.sdk.MsfMessagePair
import com.tencent.mobileqq.msf.sdk.MsfRespHandleUtil
import com.tencent.mobileqq.msf.sdk.MsfServiceSdk
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.json.arr
import me.yxp.qfun.utils.json.obj
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.walk
import mqq.app.MobileQQ
import mqq.app.msghandle.MsgRespHandler

@HookItemAnnotation("监听RKey")
object OnGetRKey : BaseApiHookItem<Listener>() {

    private const val SERVICE_CMD = "OidbSvcTrpcTcp.0x9067_202"

    var friendRkey: String = ""
        private set
    var groupRkey: String = ""
        private set

    override fun loadHook() {
        MsgRespHandler::class.java.getDeclaredMethod(
            "dispatchRespMsg",
            MobileQQ::class.java,
            MsfMessagePair::class.java,
            MsfRespHandleUtil::class.java,
            MsfServiceSdk::class.java
        ).hookAfter(this) { param ->
            val msfMessagePair = param.args[1] as MsfMessagePair
            val fromServiceMsg = msfMessagePair.fromServiceMsg

            val cmd = fromServiceMsg.serviceCmd
            if (cmd != SERVICE_CMD) return@hookAfter

            val wupBuffer = fromServiceMsg.wupBuffer

            val data = ProtoData().apply { fromBytes(wupBuffer) }
            val json = data.toJSON()

            val rkeyArray = json.walk("4", "4", "1")?.arr ?: return@hookAfter

            friendRkey = rkeyArray[0]?.obj?.get("1")?.str ?: ""

            groupRkey = rkeyArray[1]?.obj?.get("1")?.str ?: ""
        }
    }
}