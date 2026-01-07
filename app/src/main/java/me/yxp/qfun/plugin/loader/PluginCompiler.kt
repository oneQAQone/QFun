package me.yxp.qfun.plugin.loader

import bsh.BshMethod
import bsh.Interpreter
import me.yxp.qfun.hook.api.MenuClickListener
import me.yxp.qfun.hook.api.OnMenuBuild
import me.yxp.qfun.hook.api.OnPaiYiPai
import me.yxp.qfun.hook.api.OnReceiveMsg
import me.yxp.qfun.hook.api.OnSendMsg
import me.yxp.qfun.hook.api.OnTroopJoin
import me.yxp.qfun.hook.api.OnTroopQuit
import me.yxp.qfun.hook.api.OnTroopShutUp
import me.yxp.qfun.plugin.api.PluginCallback
import me.yxp.qfun.plugin.api.PluginMethod
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.plugin.bean.PluginInfo
import me.yxp.qfun.utils.log.PluginError
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.ClassUtils
import java.io.File
import java.lang.reflect.Modifier

class PluginCompiler(val info: PluginInfo) {
    var interpreter = Interpreter()
    val menuItems = linkedMapOf<String, String>()
    val msgMenuItem = mutableSetOf<String>()

    private val api = PluginMethod(this)
    val callback = PluginCallback(this)
    val loader = FixClassLoader()

    @Synchronized
    fun start() {
        if (info.isRunning) {
            stop(false)
        }

        info.updateFromDisk()

        val scriptFile = File(info.dirPath, "main.java")
        if (!scriptFile.exists()) throw IllegalStateException("main.java not found")

        try {
            interpreter = Interpreter()

            interpreter.apply {
                set("context", HostInfo.hostContext)
                set("myUin", QQCurrentEnv.currentUin)
                set("classLoader", ClassUtils.hostClassLoader)
                set("pluginPath", info.dirPath)
                set("pluginId", info.id)
                setClassLoader(loader)

                PluginMethod::class.java.declaredMethods
                    .filter { Modifier.isPublic(it.modifiers) }
                    .filterNot { it.name.contains("$") }
                    .forEach { nameSpace.setMethod(BshMethod(it, api)) }

                source(scriptFile.absolutePath)
            }
            registerCallbacks()
            info.isRunning = true
        } catch (e: Exception) {
            stop(false)
            throw e
        }
    }

    @Synchronized
    fun stop(invokeCallback: Boolean = true) {
        if (!info.isRunning) return

        if (invokeCallback) {
            try {
                callback.unLoadPlugin()
            } catch (e: Exception) {
                PluginError.callError(e, info)
            }
        }

        try {
            removeCallbacks()
            interpreter.nameSpace.clear()
            menuItems.clear()
            msgMenuItem.clear()
        } catch (e: Exception) {
            PluginError.callError(e, info)
        } finally {
            info.isRunning = false
        }
    }

    private fun registerCallbacks() {
        OnReceiveMsg.addListener(callback.receiveMsgListener)
        OnSendMsg.addListener(callback.sendMsgListener)
        OnTroopJoin.addListener(callback.troopJoinListener)
        OnTroopQuit.addListener(callback.troopQuitListener)
        OnTroopShutUp.addListener(callback.troopShutUpListener)
        OnPaiYiPai.addListener(callback.paiYiPaiListener)
        msgMenuItem.forEach { item ->
            val args = item.split(",")
            if (args.size >= 4) {
                OnMenuBuild.addListener(object : MenuClickListener {
                    override val menuKey: String get() = item
                    override fun onClick(msgData: MsgData) {
                        callback.invokeMsgMenuItem(args[3], msgData)
                    }
                })
            }
        }
    }

    private fun removeCallbacks() {
        OnReceiveMsg.removeListener(callback.receiveMsgListener)
        OnSendMsg.removeListener(callback.sendMsgListener)
        OnTroopJoin.removeListener(callback.troopJoinListener)
        OnTroopQuit.removeListener(callback.troopQuitListener)
        OnTroopShutUp.removeListener(callback.troopShutUpListener)
        OnPaiYiPai.removeListener(callback.paiYiPaiListener)
        msgMenuItem.forEach { item ->
            OnMenuBuild.listenerSet.removeIf { it.menuKey == item }
        }
    }
}