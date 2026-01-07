package me.yxp.qfun.plugin.api

import bsh.BshMethod
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.api.PaiYiPaiListener
import me.yxp.qfun.hook.api.ReceiveMsgListener
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.api.TroopJoinListener
import me.yxp.qfun.hook.api.TroopQuitListener
import me.yxp.qfun.hook.api.TroopShutUpListener
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.plugin.loader.PluginCompiler
import me.yxp.qfun.utils.log.PluginError

class PluginCallback(val compiler: PluginCompiler) {

    private val info = compiler.info
    val receiveMsgListener = ReceiveMsgListener { msgRecord ->
        runOnBackground(
            "onMsg", arrayOf(Object::class.java), arrayOf(MsgData(msgRecord))
        )
    }

    val troopJoinListener = TroopJoinListener { troopUin, memberUin ->
        runOnBackground(
            "joinGroup",
            arrayOf(String::class.java, String::class.java),
            arrayOf(troopUin, memberUin)
        )
    }

    val troopQuitListener = TroopQuitListener { troopUin, memberUin ->
        runOnBackground(
            "quitGroup",
            arrayOf(String::class.java, String::class.java),
            arrayOf(troopUin, memberUin)
        )
    }

    val troopShutUpListener = TroopShutUpListener { troopUin, memberUin, time, opUin ->
        runOnBackground(
            "shutUpGroup", arrayOf(
                String::class.java, String::class.java, Long::class.java, String::class.java
            ), arrayOf(troopUin, memberUin, time, opUin)
        )

    }

    val paiYiPaiListener = PaiYiPaiListener { peerUin, chatType, opUin ->
        runOnBackground(
            "onPaiYiPai", arrayOf(
                String::class.java, Int::class.java, String::class.java
            ), arrayOf(peerUin, chatType, opUin)
        )
    }

    val sendMsgListener = SendMsgListener { elements ->
        val interpreter = info.compiler.interpreter
        val nameSpace = interpreter.nameSpace
        if (nameSpace.methodNames.contains("getMsg")) {

            elements.mapNotNull { it.textElement }.forEach { textElement ->

                val new = runCatching {
                    nameSpace.getMethod("getMsg", arrayOf(String::class.java))
                        .invoke(arrayOf(textElement.content), interpreter) as String
                }.onFailure {
                    PluginError.callError(it, compiler.info)
                }.getOrElse { textElement.content }
                textElement.content = new
            }
        }
    }

    fun unLoadPlugin() {
        invokeMethodExists("unLoadPlugin", emptyArray(), emptyArray())
    }

    fun chatInterface(chatType: Int, peerUin: String, peerName: String) {
        runOnBackground(
            "chatInterface",
            arrayOf(Integer.TYPE, String::class.java, String::class.java),
            arrayOf(chatType, peerUin, peerName)
        )
    }

    fun invokeMsgMenuItem(methodName: String, msgData: MsgData) {
        runOnBackground(methodName, arrayOf(Object::class.java), arrayOf(msgData))
    }

    fun invokeMenuItem(
        methodName: String, chatType: Int, peerUin: String, peerName: String, contact: Any?
    ) {
        val bsh = compiler.interpreter

        ModuleScope.launchIO {
            try {
                val nameSpace = bsh.nameSpace
                var targetMethod: BshMethod? = null
                var args: Array<Any?>? = null

                for (method in nameSpace.methods) {
                    if (method.name != methodName) continue

                    val paramTypes = method.parameterTypes

                    if (paramTypes.size == 3) {
                        targetMethod = method
                        args = arrayOf(chatType, peerUin, peerName)
                        break
                    }

                    if (paramTypes.size == 4) {
                        targetMethod = method
                        args = arrayOf(chatType, peerUin, peerName, contact)
                        break
                    }
                }

                if (targetMethod != null) {
                    targetMethod.invoke(args, bsh)
                } else {
                    throw NoSuchMethodException("未找到匹配参数的方法: $methodName (需定义3参或4参)")
                }

            } catch (e: Exception) {
                if (e is NoSuchMethodException) {
                    PluginError.findError(e, info, methodName)
                } else {
                    PluginError.callError(e, info)
                }
            }
        }
    }

    private fun invokeMethodExists(
        methodName: String, paramTypes: Array<Class<*>>, args: Array<Any>
    ) {
        runCatching {
            val nameSpace = compiler.interpreter.nameSpace ?: return
            if (nameSpace.methodNames.contains(methodName)) {
                nameSpace.getMethod(methodName, paramTypes).invoke(args, compiler.interpreter)
            }
        }.onFailure {
            PluginError.callError(it, compiler.info)
        }
    }

    private fun runOnBackground(
        methodName: String, paramTypes: Array<Class<*>>, args: Array<Any>
    ) {
        ModuleScope.launchIO {
            invokeMethodExists(methodName, paramTypes, args)
        }
    }
}