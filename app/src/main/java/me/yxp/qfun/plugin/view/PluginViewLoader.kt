package me.yxp.qfun.plugin.view

import android.annotation.SuppressLint
import com.tencent.aio.data.AIOContact
import com.tencent.mobileqq.activity.ScaleAIOActivity
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

    var currentContact = PluginContact()

    override fun loadHook() {
        val string = String::class.java
        AIOContact::class.java.getDeclaredConstructor(
            Int::class.javaPrimitiveType, string, string, string
        ).hookAfter(this) { param ->

            val chatType = param.args[0] as Int
            val peerUid = param.args[1] as String
            val peerUin = if (chatType == 2) peerUid
            else FriendTool.getUinFromUid(peerUid).ifEmpty {
                val intent = QQCurrentEnv.activity?.intent ?: return@ifEmpty ""
                intent.getStringExtra("key_peerUin") ?: ""
            }

            if (chatType != 0) {
                currentContact = PluginContact(
                    chatType,
                    peerUid,
                    peerUin,
                    param.args[2] as String,
                    param.args[3] as String,
                )
            }

            if (chatType == 0 && QQCurrentEnv.activity !is ScaleAIOActivity) hideView()
            else if (currentContact.peerUid.isNotEmpty()) showView()

        }
    }

    private fun showView() {

        ModuleScope.launchMainDelayed(1) {

            val activity = QQCurrentEnv.activity ?: return@launchMainDelayed
            if (PluginManager.plugins.any { it.isRunning }) {
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