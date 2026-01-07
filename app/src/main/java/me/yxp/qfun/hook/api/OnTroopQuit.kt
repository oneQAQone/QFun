package me.yxp.qfun.hook.api

import com.tencent.mobileqq.troop.api.impl.TroopMemberInfoServiceImpl
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter

@HookItemAnnotation("监听用户退群")
object OnTroopQuit : BaseApiHookItem<TroopQuitListener>() {
    override fun loadHook() {

        TroopMemberInfoServiceImpl::class.java
            .getDeclaredMethod(
                "deleteTroopMember",
                String::class.java,
                String::class.java,
                Boolean::class.javaPrimitiveType
            )
            .hookAfter(this) { param ->
                val troopUin = param.args[0] as String
                val memberUin = param.args[1] as String
                listenerSet
                    .filter {
                        verify(it)
                    }.forEach {
                        it.onQuit(troopUin, memberUin)
                    }
            }


    }
}

fun interface TroopQuitListener : Listener {
    fun onQuit(troopUin: String, memberUin: String)
}