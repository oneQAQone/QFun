package me.yxp.qfun.hook.msg

import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.tencent.qqnt.kernel.nativeinterface.IQQNTWrapperSession
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook
import me.yxp.qfun.utils.io.ObjectStore
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.FriendTool
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.NtGrayTipJsonBuilder
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.findMethod
import top.artmoe.inao.entries.InfoSyncPushOuterClass
import top.artmoe.inao.entries.MsgPushOuterClass
import top.artmoe.inao.entries.QQMessageOuterClass
import java.util.concurrent.ConcurrentHashMap

@HookItemAnnotation(
    "防撤回",
    "拦截撤回指令并显示提示(需保活)",
    HookCategory.MSG
)
object AntiRevoke : BaseSwitchHookItem(), AIOViewUpdateListener {

    private var retractMessageMap = ConcurrentHashMap<String, MutableList<String>>()
    private const val STORE_KEY = "RetractMessageMap"
    private const val VIEW_TAG = "RevokeTagView"

    private val mapSerializer =
        MapSerializer(String.serializer(), ListSerializer(String.serializer()))

    private fun initData() {
        ObjectStore.load("data", STORE_KEY, mapSerializer)?.let { loaded ->
            loaded.forEach { (k, v) ->
                retractMessageMap[k] = v.toMutableList()
            }
        }
    }

    private fun saveData() {
        ObjectStore.save(
            "data",
            STORE_KEY,
            retractMessageMap.toMap().mapValues { it.value.toList() },
            mapSerializer
        )
    }

    override fun onHook() {
        initData()
        IQQNTWrapperSession.CppProxy::class.java
            .findMethod {
                name = "onMsfPush"
            }.hookBefore(this) { param ->
                val cmd = param.args[0] as String
                val protoBuf = param.args[1] as ByteArray

                try {
                    when (cmd) {
                        "trpc.msg.register_proxy.RegisterProxy.InfoSyncPush" -> {
                            handleInfoSyncPush(protoBuf, param)
                        }

                        "trpc.msg.olpush.OlPushService.MsgPush" -> {
                            handleMsgPush(protoBuf, param)
                        }
                    }
                } catch (t: Throwable) {
                    LogUtils.e(this, t)
                }
            }
    }

    override fun onUpdate(frameLayout: FrameLayout, msgRecord: MsgRecord) {

        val msgData = MsgData(msgRecord)
        val peerUid = msgData.peerUid
        val msgSeq = msgRecord.msgSeq.toString()

        val seqList = retractMessageMap[peerUid] ?: return

        var tipsView = frameLayout.findViewWithTag<TextView>(VIEW_TAG)
        if (tipsView == null) {
            tipsView = TextView(frameLayout.context).apply {
                tag = VIEW_TAG
                textSize = 10f
                setTextColor(Color.RED)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    topMargin = 10
                }
            }
            frameLayout.addView(tipsView)
        }

        if (seqList.contains(msgSeq)) {
            tipsView.text = "已撤回"
            tipsView.textSize = 15f
            tipsView.isVisible = true
        } else {
            tipsView.isVisible = false
        }
    }

    private fun handleInfoSyncPush(protoBuf: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val infoSyncPush = InfoSyncPushOuterClass.InfoSyncPush.parseFrom(protoBuf)
        if (!infoSyncPush.hasSyncRecallContent()) return

        val syncRecallContent = infoSyncPush.syncRecallContent
        val syncInfoBodyList = syncRecallContent.syncInfoBodyList

        var modified = false
        val newSyncRecallBuilder = syncRecallContent.toBuilder()

        for (i in syncInfoBodyList.indices) {
            val syncInfoBody = syncInfoBodyList[i]
            val msgList = syncInfoBody.msgList

            val hasRecall = msgList.any { msg ->
                val content = msg.messageContentInfo
                (content.msgType == 732 && content.msgSubType == 17) ||
                        (content.msgType == 528 && content.msgSubType == 138)
            }

            if (hasRecall) {

                val newBody = syncInfoBody.toBuilder().clearMsg().build()
                newSyncRecallBuilder.setSyncInfoBody(i, newBody)
                modified = true
            }
        }

        if (modified) {
            val newInfoSyncPush = infoSyncPush.toBuilder()
                .setSyncRecallContent(newSyncRecallBuilder)
                .build()
            param.args[1] = newInfoSyncPush.toByteArray()
        }
    }

    private fun handleMsgPush(protoBuf: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val msgPush = MsgPushOuterClass.MsgPush.parseFrom(protoBuf)
        val qqMessage = msgPush.qqMessage
        val content = qqMessage.messageContentInfo

        val msgType = content.msgType
        val msgSubType = content.msgSubType

        val opBytes = qqMessage.messageBody.operationInfo.toByteArray()

        if (msgType == 732 && msgSubType == 17) {
            onGroupRecallByMsgPush(opBytes, param)
        } else if (msgType == 528 && msgSubType == 138) {
            onC2CRecallByMsgPush(opBytes, param)
        }
    }

    private fun onGroupRecallByMsgPush(opBytes: ByteArray, param: XC_MethodHook.MethodHookParam) {

        val realBytes = if (opBytes.size > 7) opBytes.copyOfRange(7, opBytes.size) else opBytes
        val info =
            QQMessageOuterClass.QQMessage.MessageBody.GroupRecallOperationInfo.parseFrom(realBytes)

        val msgInfo = info.info.msgInfo
        val msgTime = msgInfo.msgTime
        val msgSeq = msgInfo.msgSeq
        val groupPeerId = info.peerId.toString()
        val operatorUid = info.info.operatorUid
        val senderUid = msgInfo.senderUid

        if (isTimeExpired(msgTime)) return
        if (isAlreadyRevoked(groupPeerId, msgSeq)) return

        val selfUid = FriendTool.getUidFromUin(QQCurrentEnv.currentUin)
        if (operatorUid == selfUid) return

        recordRevoke(groupPeerId, msgSeq)

        param.args[1] = ByteArray(0)

        ModuleScope.launchIO {
            val operatorName =
                TroopTool.getMemberInfo(groupPeerId, FriendTool.getUinFromUid(operatorUid)).uinName
            val senderName = if (operatorUid != senderUid) {
                TroopTool.getMemberInfo(
                    groupPeerId,
                    FriendTool.getUinFromUid(senderUid)
                ).uinName
            } else null

            val builder = NtGrayTipJsonBuilder()

            builder.append(
                NtGrayTipJsonBuilder.UserItem(
                    FriendTool.getUinFromUid(operatorUid),
                    operatorUid,
                    operatorName
                )
            )

            builder.appendText(" 尝试撤回")

            if (senderName != null) {
                builder.append(
                    NtGrayTipJsonBuilder.UserItem(
                        FriendTool.getUinFromUid(senderUid),
                        senderUid,
                        senderName
                    )
                )
                builder.appendText(" 的")
            }

            builder.append(NtGrayTipJsonBuilder.MsgRefItem("一条消息", msgSeq))

            val contact = Contact(2, groupPeerId, "")
            MsgTool.addLocalGrayTipMsg(
                contact,
                builder.build().toString(),
                NtGrayTipJsonBuilder.AIO_AV_GROUP_NOTICE
            )
        }
    }

    private fun onC2CRecallByMsgPush(opBytes: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val info =
            QQMessageOuterClass.QQMessage.MessageBody.C2CRecallOperationInfo.parseFrom(opBytes)
        val innerInfo = info.info

        val msgTime = innerInfo.msgTime
        val msgSeq = innerInfo.msgSeq
        val operatorUid = innerInfo.operatorUid

        if (isTimeExpired(msgTime)) return
        if (isAlreadyRevoked(operatorUid, msgSeq)) return

        val selfUid = FriendTool.getUidFromUin(QQCurrentEnv.currentUin)
        if (operatorUid == selfUid) return

        recordRevoke(operatorUid, msgSeq)
        param.args[1] = ByteArray(0)

        ModuleScope.launchIO {
            val builder = NtGrayTipJsonBuilder()
            builder.appendText("对方")
            builder.appendText(" 尝试撤回")
            builder.append(NtGrayTipJsonBuilder.MsgRefItem("一条消息", msgSeq))

            val contact = Contact(1, operatorUid, "")
            MsgTool.addLocalGrayTipMsg(
                contact,
                builder.build().toString(),
                NtGrayTipJsonBuilder.AIO_AV_C2C_NOTICE
            )
        }
    }

    private fun isTimeExpired(msgTime: Long): Boolean {
        return (System.currentTimeMillis() / 1000 - msgTime) > 3600
    }

    private fun isAlreadyRevoked(peerId: String, seq: Int): Boolean {
        return retractMessageMap[peerId]?.contains(seq.toString()) == true
    }

    private fun recordRevoke(peerId: String, seq: Int) {
        retractMessageMap.computeIfAbsent(peerId) { ArrayList() }
            .add(seq.toString())
        saveData()
    }
}
