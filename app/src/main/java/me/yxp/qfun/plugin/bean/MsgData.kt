package me.yxp.qfun.plugin.bean

import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import me.yxp.qfun.hook.api.OnGetRKey
import me.yxp.qfun.utils.qq.FriendTool

class MsgData(@JvmField val data: MsgRecord) {

    @JvmField
    val type: Int = data.chatType

    @JvmField
    val msgType: Int = data.msgType

    @JvmField
    val peerUin: String = data.peerUin.toString()

    @JvmField
    val peerUid: String = data.peerUid ?: ""

    @JvmField
    val userUin: String = data.senderUin.toString()

    @JvmField
    val userUid: String = data.senderUid ?: ""

    @JvmField
    val time: Long = data.msgTime

    @JvmField
    val msgId: Long = data.msgId

    @JvmField
    val contact: Contact = Contact(data.chatType, data.peerUid, data.guildId)

    @JvmField
    var msg: String = ""

    @JvmField
    val atList: ArrayList<String> = ArrayList()

    @JvmField
    val atMap: HashMap<String, String> = HashMap()

    @JvmField
    var path: String = ""

    init {
        processElements(data.elements)
    }

    private fun processElements(elements: ArrayList<MsgElement>) {
        val msgBuilder = StringBuilder()
        elements.forEach {
            when (it.elementType) {
                1 -> {
                    val textElement = it.textElement
                    msgBuilder.append(textElement.content)
                    if (textElement.atType == 2) {
                        runCatching {
                            val atUin1 = FriendTool.getUinFromUid(textElement.atNtUid)
                            val atUin = atUin1.ifEmpty { textElement.atUid.toString() }
                            atList.add(atUin)
                            atMap[atUin] = textElement.content
                        }
                    }
                }

                2 -> {
                    val rkey = if (type == 1) OnGetRKey.friendRkey else OnGetRKey.groupRkey
                    val picElement = it.picElement
                    val url = picElement.originImageUrl
                    if (!url.isNullOrEmpty()) {
                        msgBuilder.append("[pic=https://multimedia.nt.qq.com.cn$url$rkey]")
                    }
                }

                3 -> path += it.fileElement.filePath

                5 -> path += it.videoElement.filePath

                10 -> {
                    msgBuilder.append(it.arkElement.bytesData)
                }
            }
        }
        msg = msgBuilder.toString()
    }

}
