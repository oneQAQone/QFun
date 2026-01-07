package me.yxp.qfun.utils.qq

import com.tencent.mobileqq.app.CardHandler
import com.tencent.qqnt.ntrelation.friendsinfo.api.IFriendsInfoService
import com.tencent.relation.common.api.IRelationNTUinAndUidApi
import me.yxp.qfun.plugin.bean.FriendInfo
import me.yxp.qfun.utils.reflect.callOriginal
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method

object FriendTool {

    private val sendZan: Method by lazy {
        CardHandler::class.java.findMethod {
            returnType = void
            paramTypes(long, long, byteArr, int, int, int)
        }
    }

    fun getAllFriend(): List<FriendInfo> {
        val service = api<IFriendsInfoService>()
        val friendInfoList = mutableListOf<FriendInfo>()
        service.getAllFriend("").forEach {
            val info = it.toString().split(" ")
            friendInfoList.add(
                FriendInfo(
                    info[2],
                    info[4],
                    service.getNickWithUid(info[4], ""),
                    service.getRemarkWithUid(info[4], "")
                )
            )
        }
        return friendInfoList
    }

    fun isFriend(uin: String): Boolean {
        return api<IFriendsInfoService>().isFriend(getUidFromUin(uin), "")
    }

    fun getUidFromUin(uin: String): String {
        return api<IRelationNTUinAndUidApi>().getUinFromUid(uin)
    }

    fun getUinFromUid(uid: String): String {
        return api<IRelationNTUinAndUidApi>().getUinFromUid(uid)
    }

    private fun createRequestData(uin: String): ByteArray {
        return byteArrayOf(
            12, 24, 0, 1, 6, 1, 49, 22, 1, if (isFriend(uin)) 49 else 53
        )
    }

    fun sendZan(uin: String, num: Int) {

        sendZan.callOriginal(
            handler<CardHandler>(),
            QQCurrentEnv.currentUin.toLong(),
            uin.toLong(),
            createRequestData(uin),
            if (isFriend(uin)) 1 else 5,
            num,
            0
        )

    }

}

