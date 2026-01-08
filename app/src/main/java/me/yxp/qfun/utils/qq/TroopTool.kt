package me.yxp.qfun.utils.qq

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.tencent.biz.troop.EditUniqueTitleActivity
import com.tencent.mobileqq.app.QQAppInterface
import com.tencent.mobileqq.data.troop.TroopInfo
import com.tencent.mobileqq.data.troop.TroopMemberCardInfo
import com.tencent.mobileqq.data.troop.TroopMemberInfo
import com.tencent.mobileqq.troop.api.ITroopInfoService
import com.tencent.mobileqq.troop.clockin.handler.TroopClockInHandler
import com.tencent.mobileqq.troop.handler.TroopMemberCardHandler
import com.tencent.mobileqq.troop.membersetting.handler.MemberSettingHandler
import com.tencent.qqnt.troop.ITroopListRepoApi
import com.tencent.qqnt.troop.ITroopOperationRepoApi
import com.tencent.qqnt.troopmemberlist.ITroopMemberListRepoApi
import me.yxp.qfun.plugin.bean.ForbidInfo
import me.yxp.qfun.plugin.bean.GroupInfo
import me.yxp.qfun.plugin.bean.MemberInfo
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethodOrNull
import mqq.observer.BusinessObserver
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Proxy
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
object TroopTool : DexKitTask {

    private val modifyTroopShutUpTime by lazy {
        ITroopOperationRepoApi::class.java.findMethod {
            name = "modifyTroopShutUpTime"
        }
    }

    private val fetchTroopMemberList by lazy {
        ITroopMemberListRepoApi::class.java.findMethod {
            name = "fetchTroopMemberList"
            paramCount = 5
        }
    }

    private val fetchTroopMemberInfo by lazy {
        ITroopMemberListRepoApi::class.java.findMethod {
            name = "fetchTroopMemberInfo"
            paramCount = 6
        }
    }

    private val shutUp by lazy {
        val handler = MemberSettingHandler::class.java
        handler.findMethodOrNull {
            returnType = boolean
            paramTypes(string, string, long)
        } ?: handler.findMethod {
            returnType = boolean
            paramTypes(long, string, string)
        }
    }

    private val kickGroup by lazy {
        val handler = MemberSettingHandler::class.java
        handler.findMethodOrNull {
            returnType = void
            paramTypes(long, list, boolean, boolean)
        } ?: handler.findMethod {
            returnType = void
            paramTypes(long, boolean, boolean, list)
        }
    }

    private val setGroupAdmin by lazy {
        requireClass("setting").findMethod {
            returnType = void
            paramTypes(byte, string, string)
        }
    }

    private val setGroupMemberTitle by lazy {
        val editActivity = EditUniqueTitleActivity::class.java
        editActivity.findMethodOrNull {
            returnType = void
            paramTypes(
                QQAppInterface::class.java,
                string, string, string,
                BusinessObserver::class.java
            )
        } ?: editActivity.findMethod {
            returnType = void
            paramTypes(string, string, string)
        }
    }

    private val changeMemberName by lazy {
        TroopMemberCardHandler::class.java.findMethod {
            returnType = void
            paramTypes(string, arrayList, arrayList)
        }
    }

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
        modifyTroopShutUpTime.invoke(
            api<ITroopOperationRepoApi>(),
            troopUin,
            if (enable) 0x0FFFFFFF else 0,
            null,
            null
        )
    }

    fun shutUp(troopUin: String, uin: String, time: Long) {
        val handler = handler<MemberSettingHandler>()
        runCatching {
            shutUp.invoke(handler, troopUin, uin, time)
        }.onFailure {
            shutUp.invoke(handler, time, troopUin, uin)
        }
    }

    fun setGroupAdmin(troopUin: String, uin: String, enable: Boolean) {
        val byte: Byte = if (enable) 1 else 0
        setGroupAdmin.invoke(
            requireClass("setting").newInstance(),
            byte,
            troopUin,
            uin
        )
    }

    fun kickGroup(troopUin: String, uin: String, block: Boolean) {
        val handler = handler<MemberSettingHandler>()
        val uinList = ArrayList<Long>().apply { add(uin.toLong()) }
        runCatching {
            kickGroup.invoke(handler, troopUin.toLong(), uinList, block, false)
        }.onFailure {
            kickGroup.invoke(handler, troopUin.toLong(), block, false, uinList)
        }
    }

    fun setGroupMemberTitle(troopUin: String, uin: String, title: String) {

        val edit = EditUniqueTitleActivity()
        runCatching {
            setGroupMemberTitle.invoke(
                edit,
                QQCurrentEnv.qQAppInterface,
                troopUin,
                uin,
                title,
                null
            )
        }.onFailure {
            edit.app = QQCurrentEnv.qQAppInterface
            edit.intent = Intent()
            ContextWrapper::class.java
                .getDeclaredMethod(
                    "attachBaseContext",
                    Context::class.java
                )
                .apply { isAccessible = true }
                .invoke(edit, HostInfo.hostContext)
            setGroupMemberTitle.invoke(edit, troopUin, uin, title)
        }

    }

    fun changeMemberName(troopUin: String, uin: String, name: String) {
        val cardInfo = TroopMemberCardInfo().apply {
            colorNick = ""
            colorNickId = 0
            memberuin = uin
            this.name = name
            troopuin = troopUin
        }
        changeMemberName.invoke(
            handler<TroopMemberCardHandler>(),
            troopUin,
            ArrayList<TroopMemberCardInfo>().apply { add(cardInfo) },
            ArrayList<Int>().apply { add(1) }
        )
    }

    fun isShutUp(troopUin: String): Boolean {
        val info = getGroupInfo(troopUin)
        return !(info.dwGagTimeStamp == 0L && info.dwGagTimeStamp_me == 0L)
    }

    private fun processMemberInfo(troopMemberInfo: TroopMemberInfo): MemberInfo {
        return troopMemberInfo.let {
            val troopNick = it.troopnick
            val uinName = if (troopNick.isNullOrEmpty()) it.friendnick else troopNick

            MemberInfo(
                it.join_time,
                it.last_active_time,
                it.memberuin,
                it.realLevel,
                uinName,
                "${it.role}",
                it
            )

        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getMemberInfoList(troopUin: String): List<TroopMemberInfo> {
        val completableFuture = CompletableFuture<ArrayList<TroopMemberInfo>>()
        val callback = Proxy.newProxyInstance(
            ClassUtils.hostClassLoader,
            arrayOf(fetchTroopMemberList.parameterTypes[4])
        ) { _, method, args ->
            if (method.returnType == Void.TYPE && method.parameterCount == 2) {
                val list = when {
                    args[0] is ArrayList<*> -> args[0]
                    args[1] is ArrayList<*> -> args[1]
                    else -> emptyList<TroopMemberInfo>()
                }
                completableFuture.complete(list as ArrayList<TroopMemberInfo>)
            }
            0
        }
        fetchTroopMemberList.invoke(
            api<ITroopMemberListRepoApi>(),
            troopUin,
            null,
            true,
            "",
            callback
        )
        return completableFuture.get(5, TimeUnit.SECONDS)
    }

    fun getMemberInfo(troopUin: String, uin: String): MemberInfo {
        val completableFuture = CompletableFuture<TroopMemberInfo>()
        val callback = Proxy.newProxyInstance(
            ClassUtils.hostClassLoader,
            arrayOf(fetchTroopMemberInfo.parameterTypes[5])
        ) { _, method, args ->
            if (method.returnType == Void.TYPE && method.parameterTypes[0] == TroopMemberInfo::class.java) {
                completableFuture.complete(args[0] as TroopMemberInfo)
            }
            0
        }
        fetchTroopMemberInfo.invoke(
            api<ITroopMemberListRepoApi>(),
            troopUin,
            uin,
            true,
            null,
            "",
            callback
        )
        val troopMemberInfo = completableFuture.get(5, TimeUnit.SECONDS)
        return processMemberInfo(troopMemberInfo)
    }

    fun getGroupMemberList(troopUin: String): List<MemberInfo> {
        val memberList = ArrayList<MemberInfo>()
        getMemberInfoList(troopUin).forEach {
            memberList.add(processMemberInfo(it))
        }
        return memberList
    }

    fun getProhibitList(troopUin: String): List<ForbidInfo> {
        val forbidList = ArrayList<ForbidInfo>()
        getMemberInfoList(troopUin).forEach {
            val gagTime = it.gagTimeStamp
            val troopNick = it.troopnick
            val time = gagTime - System.currentTimeMillis() / 1000
            val userName = if (troopNick.isNullOrEmpty()) it.friendnick else troopNick
            if (time > 0) {
                forbidList.add(
                    ForbidInfo(it.memberuin, gagTime, time, userName)
                )
            }
        }
        return forbidList
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "setting" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.troop.membersetting.part")
            matcher {
                usingStrings("MemberSettingGroupManagePart")
            }
        }
    )

}