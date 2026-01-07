@file:Suppress("unused")

package me.yxp.qfun.plugin.api

import android.app.Activity
import bsh.classpath.BshLoaderManager
import com.tencent.mobileqq.data.troop.TroopInfo
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.plugin.bean.ForbidInfo
import me.yxp.qfun.plugin.bean.FriendInfo
import me.yxp.qfun.plugin.bean.GroupInfo
import me.yxp.qfun.plugin.bean.MemberInfo
import me.yxp.qfun.plugin.loader.JarLoader
import me.yxp.qfun.plugin.loader.PluginCompiler
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.json.JsonConfigUtils
import me.yxp.qfun.utils.log.PluginError
import me.yxp.qfun.utils.qq.CookieTool
import me.yxp.qfun.utils.qq.FriendTool
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.ClassUtils
import java.io.File

class PluginMethod(private val compiler: PluginCompiler) {

    private val configPath = "${compiler.info.dirPath}/config/"

    @JvmOverloads
    fun log(fileName: String = "log.txt", msg: String) {
        FileUtils.writeText(File(compiler.info.dirPath, fileName), "$msg\n", true)
    }

    fun getNowActivity(): Activity? = QQCurrentEnv.activity

    fun toast(msg: Any?) {
        Toasts.toast("$msg")
    }

    fun qqToast(icon: Int, msg: Any?) {
        Toasts.qqToast(icon, "$msg")
    }

    fun addItem(name: String, callback: String) {
        compiler.menuItems[name] = callback
    }

    @JvmOverloads
    fun addMenuItem(name: String, callback: String, msgTypes: IntArray = intArrayOf()) {
        val types = msgTypes.joinToString(",")
        compiler.msgMenuItem.add("[QFun],${compiler.info.id},$name,$callback,$types")
    }

    fun loadJava(path: String) {
        runWithErrorHandle { compiler.interpreter.source(path) }
    }

    fun loadJar(path: String) {
        runWithErrorHandle {
            compiler.loader.addClassLoader(JarLoader.loadJar(path))
        }
    }

    fun loadDex(path: String) {
        runWithErrorHandle {
            compiler.loader.addClassLoader(
                BshLoaderManager.getDexLoader(path, ClassUtils.hostClassLoader)
            )
        }
    }

    fun getAllFriend(): List<FriendInfo>? = runWithErrorHandle(null, FriendTool::getAllFriend)

    fun isFriend(uin: String): Boolean = runWithErrorHandle(false) { FriendTool.isFriend(uin) }

    fun getUidFromUin(uin: String): String =
        runWithErrorHandle("") { FriendTool.getUidFromUin(uin) }

    fun getUinFromUid(uid: String): String =
        runWithErrorHandle("") { FriendTool.getUinFromUid(uid) }

    fun sendZan(uin: String, num: Int) {
        runWithErrorHandle { FriendTool.sendZan(uin, num) }
    }

    fun clockIn(troopUin: String) {
        runWithErrorHandle { TroopTool.clockIn(troopUin) }
    }

    fun getGroupList(): List<GroupInfo>? = runWithErrorHandle(null, TroopTool::getGroupList)

    fun getGroupInfo(troopUin: String): TroopInfo? =
        runWithErrorHandle(null) { TroopTool.getGroupInfo(troopUin) }

    fun getGroupMemberList(troopUin: String): List<MemberInfo>? =
        runWithErrorHandle(null) { TroopTool.getGroupMemberList(troopUin) }

    fun getProhibitList(troopUin: String): List<ForbidInfo>? =
        runWithErrorHandle(null) { TroopTool.getProhibitList(troopUin) }

    fun getMemberInfo(troopUin: String, uin: String): MemberInfo? =
        runWithErrorHandle(null) { TroopTool.getMemberInfo(troopUin, uin) }

    fun isShutUp(troopUin: String): Boolean =
        runWithErrorHandle(false) { TroopTool.isShutUp(troopUin) }

    fun shutUpAll(troopUin: String, enable: Boolean) {
        runWithErrorHandle { TroopTool.shutUpAll(troopUin, enable) }
    }

    fun shutUp(troopUin: String, uin: String, time: Long) {
        runWithErrorHandle { TroopTool.shutUp(troopUin, uin, time) }
    }

    fun setGroupAdmin(troopUin: String, uin: String, enable: Boolean) {
        runWithErrorHandle { TroopTool.setGroupAdmin(troopUin, uin, enable) }
    }

    fun setGroupMemberTitle(troopUin: String, uin: String, title: String) {
        ModuleScope.launchMain {
            runWithErrorHandle { TroopTool.setGroupMemberTitle(troopUin, uin, title) }
        }
    }

    fun changeMemberName(troopUin: String, uin: String, name: String) {
        runWithErrorHandle { TroopTool.changeMemberName(troopUin, uin, name) }
    }

    fun kickGroup(troopUin: String, uin: String, block: Boolean) {
        runWithErrorHandle { TroopTool.kickGroup(troopUin, uin, block) }
    }

    fun sendPai(toUin: String, peerUin: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendPai(toUin, peerUin, chatType) }
    }

    fun recallMsg(chatType: Int, peerUin: String, msgId: Long) {
        runWithErrorHandle { MsgTool.recallMsg(chatType, peerUin, msgId) }
    }

    fun recallMsg(contact: Contact, msgId: Long) {
        runWithErrorHandle { MsgTool.recallMsg(contact, msgId) }
    }

    fun sendMsg(peerUin: String, msg: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendMsg(peerUin, msg, chatType) }
    }

    fun sendMsg(contact: Contact, msg: String) {
        runWithErrorHandle { MsgTool.sendMsg(contact, msg) }
    }

    fun sendPic(peerUin: String, path: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendPic(peerUin, path, chatType) }
    }

    fun sendPic(contact: Contact, path: String) {
        runWithErrorHandle { MsgTool.sendPic(contact, path) }
    }

    fun sendPtt(peerUin: String, path: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendPtt(peerUin, path, chatType) }
    }

    fun sendPtt(contact: Contact, path: String) {
        runWithErrorHandle { MsgTool.sendPtt(contact, path) }
    }

    fun sendCard(peerUin: String, data: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendCard(peerUin, data, chatType) }
    }

    fun sendCard(contact: Contact, data: String) {
        runWithErrorHandle { MsgTool.sendCard(contact, data) }
    }

    fun sendVideo(peerUin: String, path: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendVideo(peerUin, path, chatType) }
    }

    fun sendVideo(contact: Contact, path: String) {
        runWithErrorHandle { MsgTool.sendVideo(contact, path) }
    }

    fun sendFile(peerUin: String, path: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendFile(peerUin, path, chatType) }
    }

    fun sendFile(contact: Contact, path: String) {
        runWithErrorHandle { MsgTool.sendFile(contact, path) }
    }

    fun sendReplyMsg(peerUin: String, replyMsgId: Long, msg: String, chatType: Int) {
        runWithErrorHandle { MsgTool.sendReplyMsg(peerUin, replyMsgId, msg, chatType) }
    }

    fun sendReplyMsg(contact: Contact, replyMsgId: Long, msg: String) {
        runWithErrorHandle { MsgTool.sendReplyMsg(contact, replyMsgId, msg) }
    }

    fun putString(configName: String, key: String, value: String) {
        runWithErrorHandle { JsonConfigUtils.putString(configPath, configName, key, value) }
    }

    fun putInt(configName: String, key: String, value: Int) {
        runWithErrorHandle { JsonConfigUtils.putInt(configPath, configName, key, value) }
    }

    fun putBoolean(configName: String, key: String, value: Boolean) {
        runWithErrorHandle { JsonConfigUtils.putBoolean(configPath, configName, key, value) }
    }

    fun putLong(configName: String, key: String, value: Long) {
        runWithErrorHandle { JsonConfigUtils.putLong(configPath, configName, key, value) }
    }

    fun getString(configName: String, key: String, defaultValue: String): String =
        runWithErrorHandle(defaultValue) {
            JsonConfigUtils.getString(
                configPath, configName, key, defaultValue
            )
        }

    fun getInt(configName: String, key: String, defaultValue: Int): Int =
        runWithErrorHandle(defaultValue) {
            JsonConfigUtils.getInt(
                configPath, configName, key, defaultValue
            )
        }

    fun getBoolean(configName: String, key: String, defaultValue: Boolean): Boolean =
        runWithErrorHandle(defaultValue) {
            JsonConfigUtils.getBoolean(
                configPath, configName, key, defaultValue
            )
        }

    fun getLong(configName: String, key: String, defaultValue: Long): Long =
        runWithErrorHandle(defaultValue) {
            JsonConfigUtils.getLong(
                configPath, configName, key, defaultValue
            )
        }

    fun getRealSkey(): String? = runWithErrorHandle("", CookieTool::getRealSkey)

    fun getSkey(): String? = runWithErrorHandle("", CookieTool::getSkey)

    fun getStweb(): String? = runWithErrorHandle("", CookieTool::getStweb)

    fun getPt4Token(url: String): String? = runWithErrorHandle("") { CookieTool.getPt4Token(url) }

    fun getGTK(url: String): String = runWithErrorHandle("") { CookieTool.getGTK(url) }

    fun getBkn(key: String): Long = runWithErrorHandle(0L) { CookieTool.getBkn(key) }

    fun getGroupRKey(): String = runWithErrorHandle("", CookieTool::getGroupRKey)

    fun getFriendRKey(): String = runWithErrorHandle("", CookieTool::getFriendRKey)

    fun getPskey(url: String): String? = runWithErrorHandle("") { CookieTool.getPskey(url) }

    private inline fun <T> runWithErrorHandle(default: T, function: () -> T): T = runCatching {
        function()
    }.onFailure {
        PluginError.callError(it, compiler.info)
    }.getOrElse { default }

    private inline fun runWithErrorHandle(function: () -> Unit) = try {
        function()
    } catch (t: Throwable) {
        PluginError.callError(t, compiler.info)
    }


}