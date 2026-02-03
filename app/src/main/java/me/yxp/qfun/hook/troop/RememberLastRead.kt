package me.yxp.qfun.hook.troop

import android.widget.FrameLayout
import com.tencent.mvi.base.route.MsgIntent
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.view.PluginViewLoader
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.io.ObjectStore
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.newInstanceWithArgs
import me.yxp.qfun.utils.reflect.toClass
import java.lang.reflect.Constructor
import java.lang.reflect.Method

@HookItemAnnotation(
    "记住上次查看位置",
    "记住群聊上次查看位置，进入聊天界面自动跳转",
    HookCategory.GROUP
)
object RememberLastRead : BaseSwitchHookItem(), AIOViewUpdateListener {

    private lateinit var sendIntent: Method
    private lateinit var cotr: Constructor<*>

    private val serializer = MapSerializer(String.serializer(), Long.serializer())
    private var seqMap = mutableMapOf<String, Long>()

    override fun onUpdate(
        frameLayout: FrameLayout,
        msgRecord: MsgRecord
    ) {
        if (msgRecord.chatType != 2) return
        seqMap[msgRecord.peerUid] = msgRecord.msgSeq
    }

    override fun onInit(): Boolean {

        val vmMessenger = "com.tencent.mvi.base.route.VMMessenger".toClass
        cotr = vmMessenger.constructors.single {
            it.parameterCount == 2 && it.parameterTypes.contains(Boolean::class.javaPrimitiveType)
        }
        sendIntent = vmMessenger.findMethod {
            returnType = void
            paramTypes(MsgIntent::class.java)
        }
        ObjectStore.load("data", name, serializer)?.let { seqMap = it as MutableMap<String, Long> }
        return super.onInit()
    }

    override fun onHook() {

        cotr.hookAfter(this) {
            val contact = PluginViewLoader.currentContact
            if (contact.chatType != 2 || !seqMap.contains(contact.peerUid)) return@hookAfter

            ObjectStore.save("data", name, seqMap, serializer)
            val intent = $$"com.tencent.mobileqq.aio.event.MsgNavigationEvent$NavigateBySeqEvent"
                .toClass.newInstanceWithArgs("", seqMap[contact.peerUid], 0L, false, null, false, false, null, 228, null)

            ModuleScope.launchMainDelayed(100) {
                sendIntent.invoke(it.thisObject, intent)
            }

        }

    }

}