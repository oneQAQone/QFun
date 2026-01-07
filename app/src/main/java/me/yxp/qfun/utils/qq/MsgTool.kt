package me.yxp.qfun.utils.qq

import com.tencent.mobileqq.paiyipai.PaiYiPaiHandler
import com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService
import com.tencent.qqnt.kernel.nativeinterface.MsgAttributeInfo
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement
import com.tencent.qqnt.msg.api.IMsgUtilApi
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.net.HttpUtils
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.callOriginal
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethodOrNull
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import java.io.File

object MsgTool : DexKitTask {

    private val msgUtilApiImpl by lazy {
        api<IMsgUtilApi>()
    }

    private val sendPai by lazy {
        val handler = PaiYiPaiHandler::class.java
        handler.findMethodOrNull {
            returnType = void
            paramTypes(string, string, int, int)
        } ?: handler.findMethod {
            returnType = void
            paramTypes(int, int, string, string)
        }
    }

    private val sendMsg by lazy {
        IKernelMsgService::class.java
            .findMethod {
                name = "sendMsg"
            }
    }

    private fun handlePicPath(path: String): String? {
        if (path.startsWith("http")) {

            val fileName = "net_img_${System.currentTimeMillis()}_${path.hashCode()}.jpg"
            val savePath = "${QQCurrentEnv.currentDir}cache/images/$fileName"

            if (HttpUtils.downloadSync(path, savePath)) {
                return savePath
            }
            LogUtils.e("MsgTool", Throwable("下载图片失败: $path"))
            return null
        }

        return if (File(path).exists()) path else null
    }

    fun sendPai(toUin: String, peerUin: String, chatType: Int) {
        val handler = handler<PaiYiPaiHandler>()
        runCatching {
            sendPai.callOriginal(handler, toUin, peerUin, chatType, 1)
        }.onFailure {
            sendPai.callOriginal(handler, chatType, 1, toUin, peerUin)
        }
    }

    private fun recallMsg(contact: Contact, msgIds: ArrayList<Long>) {
        QQCurrentEnv.kernelMsgService!!.recallMsg(contact, msgIds, null)
    }

    fun recallMsg(contact: Contact, msgId: Long) {
        recallMsg(contact, ArrayList<Long>().apply { add(msgId) })
    }

    fun recallMsg(chatType: Int, peerUin: String, msgId: Long) {
        recallMsg(makeContact(peerUin, chatType), msgId)
    }

    fun addLocalGrayTipMsg(contact: Contact, jsonStr: String, busiId: Long) {
        try {
            val grayElement = JsonGrayElement(
                busiId,
                jsonStr,
                "",
                false,
                null
            )
            QQCurrentEnv.kernelMsgService?.addLocalJsonGrayTipMsg(
                contact,
                grayElement,
                true,
                true,
                null
            )
        } catch (t: Throwable) {
            LogUtils.e("NtGrayTip", t)
        }
    }

    private fun sendMsg(contact: Contact, elements: ArrayList<MsgElement>) {
        if (elements.isEmpty()) return

        val service = QQCurrentEnv.kernelMsgService
            ?: throw UninitializedPropertyAccessException("未获取到msgService！")
        val msgId = service.generateMsgUniqueId(contact.chatType, System.currentTimeMillis())
        sendMsg.callOriginal(
            service,
            msgId,
            contact,
            elements,
            HashMap<Int, MsgAttributeInfo>(),
            null
        )
    }

    fun sendMsg(chatType: Int, peerUin: String, elements: ArrayList<MsgElement>) {
        sendMsg(makeContact(peerUin, chatType), elements)
    }

    fun sendMsg(contact: Contact, msg: String) {
        sendMsg(contact, processMessageContent(contact, msg))
    }

    fun sendMsg(peerUin: String, msg: String, chatType: Int) {
        sendMsg(makeContact(peerUin, chatType), msg)
    }

    @Suppress("DEPRECATION")
    private fun sendMsgByType(contact: Contact, value: String, type: String) {

        val msgElement = when (type) {
            "pic" -> {

                val path = handlePicPath(value) ?: return
                msgUtilApiImpl.createPicElement(path, true, 0)
            }

            "ptt" -> msgUtilApiImpl.createPttElement(value, 0, ArrayList<Byte>())
            "video" -> msgUtilApiImpl.createVideoElement(value)
            "file" -> msgUtilApiImpl.createFileElement(value)
            "ark" -> {
                val data = requireClass("json").newInstance()
                requireClass("json").findMethod {
                    returnType = boolean
                    paramTypes(string)
                }.invoke(data, value)
                msgUtilApiImpl.callMethod("createArkElement", data) as MsgElement
            }

            else -> msgUtilApiImpl.createTextElement(value)
        }
        sendMsg(contact, ArrayList<MsgElement>().apply { add(msgElement) })
    }

    private fun sendMsgByType(peerUin: String, chatType: Int, value: String, type: String) {
        sendMsgByType(makeContact(peerUin, chatType), value, type)
    }

    fun sendPic(peerUin: String, path: String, chatType: Int) {
        sendMsgByType(peerUin, chatType, path, "pic")
    }

    fun sendPic(contact: Contact, path: String) {
        sendMsgByType(contact, path, "pic")
    }

    fun sendPtt(contact: Contact, path: String) {
        sendMsgByType(contact, path, "ptt")
    }

    fun sendPtt(peerUin: String, path: String, chatType: Int) {
        sendMsgByType(peerUin, chatType, path, "ptt")
    }

    fun sendCard(contact: Contact, data: String) {
        sendMsgByType(contact, data, "ark")
    }

    fun sendCard(peerUin: String, data: String, chatType: Int) {
        sendMsgByType(peerUin, chatType, data, "ark")
    }

    fun sendVideo(contact: Contact, path: String) {
        sendMsgByType(contact, path, "video")
    }

    fun sendVideo(peerUin: String, path: String, chatType: Int) {
        sendMsgByType(peerUin, chatType, path, "video")
    }

    fun sendFile(contact: Contact, path: String) {
        sendMsgByType(contact, path, "file")
    }

    fun sendFile(peerUin: String, path: String, chatType: Int) {
        sendMsgByType(peerUin, chatType, path, "file")
    }

    fun sendReplyMsg(contact: Contact, replyMsgId: Long, msg: String) {
        sendMsg(contact, ArrayList<MsgElement>().apply {
            add(msgUtilApiImpl.createReplyElement(replyMsgId))
            addAll(processMessageContent(contact, msg))
        })
    }

    fun sendReplyMsg(peerUin: String, replyMsgId: Long, msg: String, chatType: Int) {
        sendReplyMsg(makeContact(peerUin, chatType), replyMsgId, msg)
    }

    fun processMessageContent(contact: Contact, msg: String): ArrayList<MsgElement> =
        processMessageParts(msg).mapNotNull { (type, value) ->
            when (type) {
                "text" -> msgUtilApiImpl.createTextElement(value)
                "atUin" -> if (contact.chatType == 2) {
                    val atType = if (value == "0") 1 else 2
                    val uid = if (value == "0") "0" else FriendTool.getUidFromUin(value)
                    msgUtilApiImpl.createAtTextElement("@全体成员", uid, atType)
                } else null

                "pic" -> {

                    val path = handlePicPath(value)
                    if (path != null) {
                        msgUtilApiImpl.createPicElement(path, true, 0)
                    } else {

                        msgUtilApiImpl.createTextElement("[图片下载失败]")
                    }
                }

                else -> null
            }
        } as ArrayList<MsgElement>

    private fun processMessageParts(input: String): List<Pair<String, String>> =
        splitMessageString(input).map { part ->
            if (part.startsWith('[') && part.endsWith(']')) {
                Regex("\\[(atUin|pic)=([^]]*)]").matchEntire(part)?.let {
                    it.groupValues[1] to it.groupValues[2]
                } ?: ("text" to part)
            } else {
                "text" to part
            }
        }

    private fun splitMessageString(input: String): List<String> {
        val result = mutableListOf<String>()
        var lastEnd = 0

        Regex("\\[atUin=\\d+]|\\[pic=.*?]").findAll(input).forEach { match ->
            if (match.range.first > lastEnd) {
                result.add(input.substring(lastEnd, match.range.first))
            }
            result.add(match.value)
            lastEnd = match.range.last + 1
        }

        if (lastEnd < input.length) result.add(input.substring(lastEnd))
        return result
    }

    private fun makeContact(peerUin: String, chatType: Int): Contact {
        val peerUid = when (chatType) {
            1, 100 -> FriendTool.getUidFromUin(peerUin)
            2 -> peerUin
            else -> throw IllegalStateException("不支持的聊天类型")
        }
        return Contact(chatType, peerUid, "")
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "json" to FindClass().apply {
            searchPackages("com.tencent.qqnt.msg")
            matcher {
                usingStrings("ArkMsgModel", "toAppXml fail, metaList, err=")
            }
        }
    )
}