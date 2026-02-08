package me.yxp.qfun.plugin.view

import android.annotation.SuppressLint
import com.tencent.mobileqq.activity.ScaleAIOActivity
import com.tencent.qqnt.aio.activity.AIODelegate
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.plugin.loader.PluginManager
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.qq.FriendTool
import me.yxp.qfun.utils.qq.QQCurrentEnv

@HookItemAnnotation("监听聊天界面")
object PluginViewLoader : BaseApiHookItem<Listener>() {

    data class PluginContact(
        val chatType: Int = 0,
        val peerUid: String = "",
        val peerUin: String = "",
        val guild: String = "",
        val peerName: String = ""
    )

    @SuppressLint("StaticFieldLeak")
    private var currentPluginView: PluginView? = null
    private var aioDelegate: AIODelegate? = null

    val currentContact: PluginContact
        get() = aioDelegate?.let {
            parseToPluginContact(it.aioContact.toString())
        } ?: PluginContact()

    override fun loadHook() {

        AIODelegate::class.java.getDeclaredMethod("show")
            .hookAfter(this) {
                aioDelegate = it.thisObject as AIODelegate

                if (currentContact.chatType != 0) {
                    hideView()
                    showView()
                }


            }
        AIODelegate::class.java.getDeclaredMethod("hide")
            .hookAfter(this) {
                if (QQCurrentEnv.activity !is ScaleAIOActivity) hideView()
            }

    }

    private fun parseToPluginContact(input: String): PluginContact {
        val regex = """(\w+)=([^,)]*)""".toRegex()
        val map = regex.findAll(input).associate {
            it.groupValues[1] to it.groupValues[2].trim('\'')
        }

        val chatType = map["chatType"]?.toIntOrNull() ?: 0
        val peerUid = map["peerUid"] ?: ""
        val guild = map["guildId"] ?: ""
        val peerName = map["nick"] ?: ""

        val peerUin = if (chatType == 2) {
            peerUid
        } else {
            FriendTool.getUinFromUid(peerUid).ifEmpty {
                QQCurrentEnv.activity?.intent?.getStringExtra("key_peerUin") ?: ""
            }
        }

        return PluginContact(
            chatType,
            peerUid,
            peerUin,
            guild,
            peerName
        )
    }

    private fun showView() {

        ModuleScope.launchMainDelayed(1) {
            val activity = QQCurrentEnv.activity ?: return@launchMainDelayed

            if (PluginManager.plugins.any { it.isRunning && it.compiler.menuItems.isNotEmpty() }) {
                currentPluginView = PluginView(activity)
                currentPluginView?.show()
            }
        }
    }

    private fun hideView() {
        currentPluginView?.dismiss()
        currentPluginView = null
    }

}