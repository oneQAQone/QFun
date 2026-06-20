package me.yxp.qfun.utils.qq

import com.tencent.mobileqq.data.troop.TroopInfo
import com.tencent.mobileqq.troop.api.ITroopInfoService
import com.tencent.mobileqq.troop.clockin.handler.TroopClockInHandler
import com.tencent.qphone.base.remote.ToServiceMsg
import com.tencent.qqnt.kernel.nativeinterface.GroupMemberShutUpInfo
import com.tencent.qqnt.kernelpublic.nativeinterface.MemberRole
import com.tencent.qqnt.troop.ITroopListRepoApi
import kotlinx.coroutines.suspendCancellableCoroutine
import me.yxp.qfun.plugin.bean.ForbidInfo
import me.yxp.qfun.plugin.bean.GroupInfo
import me.yxp.qfun.plugin.bean.MemberInfo
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.reflect.findMethod
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import com.tencent.qqnt.kernel.nativeinterface.MemberInfo as NtMemberInfo

object TroopTool {

    private val clockIn by lazy {
        TroopClockInHandler::class.java
            .findMethod {
                returnType = void
                paramTypes(string, string)
            }
    }

    fun clockIn(troopUin: String) {
        clockIn.invoke(
            handler<TroopClockInHandler>(),
            troopUin,
            QQCurrentEnv.currentUin
        )
    }

    fun getGroupList(): List<GroupInfo> {
        val groupInfoList = mutableListOf<GroupInfo>()
        api<ITroopListRepoApi>().sortedJoinedTroopInfoFromCache
            .forEach {
                groupInfoList.add(
                    GroupInfo(
                        it.troopuin,
                        it.troopNameFromNT ?: it.troopuin,
                        it.troopowneruin,
                        it
                    )
                )
            }
        return groupInfoList
    }

    fun getGroupInfo(troopUin: String): TroopInfo {
        return runtime<ITroopInfoService>().getTroopInfo(troopUin)
    }

    fun shutUpAll(troopUin: String, enable: Boolean) {
        val service = QQCurrentEnv.kernelGroupService

        service?.setGroupShutUp(
            troopUin.toLong(),
            enable
        ) { errCode, errMsg ->
            if (errCode == 0) {
                val actionText = if (enable) "已开启全体禁言" else "已关闭全体禁言"
                Toasts.qqToast(2, actionText)
            } else {
                Toasts.qqToast(1, "操作失败: $errMsg ($errCode)")
            }
        }
    }

    fun shutUp(troopUin: String, uin: String, time: Long) {
        val service = QQCurrentEnv.kernelGroupService

        val gagInfo = GroupMemberShutUpInfo().apply {
            this.uid = FriendTool.getUidFromUin(uin)
            this.timeStamp = time.toInt()
        }
        val gagList = arrayListOf(gagInfo)

        service?.setMemberShutUp(
            troopUin.toLong(),
            gagList
        ) { errCode, errMsg ->
            if (errCode == 0) {
                val actionText = if (time == 0L) "已解除禁言" else "禁言成功"
                Toasts.qqToast(2, actionText)
            } else {
                Toasts.qqToast(1, "禁言失败: $errMsg ($errCode)")
            }
        }
    }

    fun setGroupAdmin(troopUin: String, uin: String, enable: Boolean) {

        if (HostInfo.isTIM) {
            val body = ByteBuffer.allocate(9).apply {
                putInt(troopUin.toLong().toInt())
                putInt(uin.toLong().toInt())
                put((if (enable) 1 else 0).toByte())
            }.array()

            val wupBuffer = ByteArrayOutputStream().apply {
                write(0x08)
                write(0xDC)
                write(0x0A)
                write(0x10)
                write(0x01)
                write(0x22)
                write(0x09)
                write(body)
            }.toByteArray()

            val toServiceMsg = ToServiceMsg(
                "mobileqq.service",
                QQCurrentEnv.currentUin,
                "OidbSvc.0x55c_1"
            ).apply {
                putWupBuffer(wupBuffer)
                addAttribute("req_pb_protocol_flag", true)
            }

            QQCurrentEnv.qQAppInterface.sendToService(toServiceMsg)
            val actionText = if (enable) "设置管理成功" else "取消管理成功"
            Toasts.qqToast(2, actionText)
            return
        }

        val service = QQCurrentEnv.kernelGroupService

        val targetRole = if (enable) MemberRole.ADMIN else MemberRole.MEMBER

        service?.modifyMemberRole(
            troopUin.toLong(),
            FriendTool.getUidFromUin(uin),
            targetRole
        ) { errCode, errMsg ->
            if (errCode == 0) {
                val actionText = if (enable) "设置管理成功" else "取消管理成功"
                Toasts.qqToast(2, actionText)
            } else {
                Toasts.qqToast(1, "操作失败: $errMsg ($errCode)")
            }
        }
    }

    fun kickGroup(troopUin: String, uin: String, block: Boolean) {
        val service = QQCurrentEnv.kernelGroupService

        val memberUids = arrayListOf(FriendTool.getUidFromUin(uin))

        service?.kickMember(
            troopUin.toLong(),
            memberUids,
            block,
            ""
        ) { errCode, errMsg, _ ->
            if (errCode == 0) {
                val actionText = if (block) "黑踢群员成功" else "已将群员踢出"
                Toasts.qqToast(2, actionText)
            } else {
                Toasts.qqToast(1, "踢人失败: $errMsg ($errCode)")
            }
        }
    }

    fun changeMemberName(troopUin: String, uin: String, name: String) {
        val service = QQCurrentEnv.kernelGroupService

        service?.modifyMemberCardName(
            troopUin.toLong(),
            FriendTool.getUidFromUin(uin),
            name
        ) { errCode, errMsg ->
            if (errCode == 0) {
                Toasts.qqToast(2, "修改群名片成功")
            } else {
                Toasts.qqToast(1, "修改失败: $errMsg ($errCode)")
            }
        }
    }

    fun setGroupMemberTitle(troopUin: String, uin: String, title: String) {
        val jsonStr = """
            {
                "1": 2300,
                "2": 2,
                "4": {
                    "1": $troopUin,
                    "3": {
                        "1": $uin,
                        "5": "$title",
                        "6": 4294967295
                    }
                }
            }
        """.trimIndent()

        val bytes = ProtoData().apply {
            fromJSON(JSONObject(jsonStr))
        }.toBytes()

        val toServiceMsg = ToServiceMsg(
            "mobileqq.service",
            QQCurrentEnv.currentUin,
            "OidbSvc.0x8fc_2"
        ).apply {
            putWupBuffer(bytes)
            addAttribute("req_pb_protocol_flag", true)
        }

        QQCurrentEnv.qQAppInterface.sendToService(toServiceMsg)
        Toasts.qqToast(2, "设置头衔成功")
    }

    fun isShutUp(troopUin: String): Boolean {
        val info = getGroupInfo(troopUin)
        return !(info.dwGagTimeStamp == 0L && info.dwGagTimeStamp_me == 0L)
    }

    private fun processNewMemberInfo(newInfo: NtMemberInfo): MemberInfo {
        val uinName = newInfo.cardName.ifEmpty { newInfo.nick }
        return MemberInfo(
            newInfo.joinTime.toLong(),
            newInfo.lastSpeakTime.toLong(),
            newInfo.uin.toString(),
            newInfo.memberRealLevel,
            uinName,
            "${newInfo.role}",
            newInfo
        )
    }

    private suspend fun getMemberInfoList(troopUin: String): List<NtMemberInfo> =
        suspendCancellableCoroutine { continuation ->
            val service = QQCurrentEnv.kernelGroupService

            if (service == null) {
                continuation.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            service.getAllMemberList(
                troopUin.toLong(),
                false
            ) { errCode, _, result ->
                if (errCode == 0 && result != null) {
                    continuation.resume(ArrayList(result.infos.values))
                } else {
                    continuation.resume(emptyList())
                }
            }
        }

    suspend fun getMemberInfo(troopUin: String, uin: String): MemberInfo =
        suspendCancellableCoroutine { continuation ->
            val service = QQCurrentEnv.kernelGroupService

            val emptyResult = MemberInfo(0, 0, uin, 0, "", "", NtMemberInfo())

            if (service == null) {
                continuation.resume(emptyResult)
                return@suspendCancellableCoroutine
            }

            val memberUid = FriendTool.getUidFromUin(uin)
            val uidsList = arrayListOf(memberUid)

            service.getMemberInfoForMqq(
                troopUin.toLong(),
                uidsList,
                false
            ) { errCode, _, result ->
                if (errCode == 0 && result != null) {
                    val newInfo = result.infos[memberUid]
                    if (newInfo != null) {
                        continuation.resume(processNewMemberInfo(newInfo))
                        return@getMemberInfoForMqq
                    }
                }
                continuation.resume(emptyResult)
            }
        }

    suspend fun getGroupMemberList(troopUin: String): List<MemberInfo> {
        val memberList = ArrayList<MemberInfo>()
        getMemberInfoList(troopUin).forEach {
            memberList.add(processNewMemberInfo(it))
        }
        return memberList
    }

    suspend fun getProhibitList(troopUin: String): List<ForbidInfo> {
        val forbidList = ArrayList<ForbidInfo>()
        getMemberInfoList(troopUin).forEach {
            val gagTime = it.shutUpTime.toLong()
            val time = gagTime - System.currentTimeMillis() / 1000
            val userName = it.cardName.ifEmpty { it.nick }
            if (time > 0) {
                forbidList.add(
                    ForbidInfo(it.uin.toString(), gagTime, time, userName)
                )
            }
        }
        return forbidList
    }

}