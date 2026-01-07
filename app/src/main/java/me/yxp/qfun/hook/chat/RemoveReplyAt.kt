package me.yxp.qfun.hook.chat

import com.tencent.mobileqq.aio.event.AIOMsgSendEvent
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mvi.base.route.MsgIntent
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import org.luckypray.dexkit.query.matchers.MethodMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "去除回复自动艾特",
    "引用消息时不再自动添加艾特",
    HookCategory.CHAT
)
object RemoveReplyAt : BaseSwitchHookItem(), DexKitTask {

    private lateinit var handleMsgIntent: Method

    override fun onInit(): Boolean {
        handleMsgIntent =
            requireClass("reply").findMethod {
                returnType = void
                paramTypes(MsgIntent::class.java)
            }
        return super.onInit()
    }

    override fun onHook() {

        handleMsgIntent.hookReplace(this) { param ->

            if (param.args[0] !is AIOMsgSendEvent.MsgOnClickReplyEvent)
                return@hookReplace param.invokeOriginal()
            val aioMsgItem = param.args[0].getObjectByTypeOrNull<AIOMsgItem>()
                ?: return@hookReplace param.invokeOriginal()
            val msgRecord = aioMsgItem.msgRecord
            val senderUid = msgRecord.senderUid
            msgRecord.senderUid = ""
            param.invokeOriginal()
            msgRecord.senderUid = senderUid

        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "reply" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.input.reply")
            matcher {
                methods {
                    methods(
                        listOf(
                            MethodMatcher().name("onDestroy"),
                            MethodMatcher().returnType(Set::class.java)
                        )
                    )
                }
            }
        }
    )

}