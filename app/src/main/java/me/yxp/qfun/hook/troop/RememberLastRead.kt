package me.yxp.qfun.hook.troop

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import com.tencent.mvi.base.route.MsgIntent
import com.tencent.qqnt.aio.activity.AIODelegate
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.RememberLastReadConfig
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.plugin.view.PluginViewLoader
import me.yxp.qfun.ui.pages.configs.RememberLastReadPage
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.io.ObjectStore
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.newInstanceWithArgs
import me.yxp.qfun.utils.reflect.toClass
import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.Method

@HookItemAnnotation(
    "记住上次查看位置",
    "记住群聊上次查看位置，进入聊天界面可自动跳转或点击跳转",
    HookCategory.GROUP
)
object RememberLastRead :
    BaseClickableHookItem<RememberLastReadConfig>(RememberLastReadConfig.serializer()),
    AIOViewUpdateListener {

    override val defaultConfig = RememberLastReadConfig()

    private lateinit var sendIntent: Method
    private lateinit var cotr: Constructor<*>

    private val seqSerializer = MapSerializer(String.serializer(), Long.serializer())
    private var seqMap = mutableMapOf<String, Long>()

    private var messenger: Any? = null
    private var chip: RememberLastReadChip? = null

    private var pendingSeq: Long? = null

    private val seqKey get() = "${name}_seq"

    override fun onUpdate(
        frameLayout: FrameLayout,
        msgRecord: MsgRecord
    ) {
        if (msgRecord.chatType != 2 || isFromHistory()) return
        seqMap[msgRecord.peerUid] = msgRecord.msgSeq
    }

    override fun onInit(): Boolean {

        loadSeqMap()

        val vmMessenger = "com.tencent.mvi.base.route.VMMessenger".toClass
        cotr = vmMessenger.constructors.single {
            it.parameterCount == 2 && it.parameterTypes.contains(Boolean::class.javaPrimitiveType)
        }
        sendIntent = vmMessenger.findMethod {
            returnType = void
            paramTypes(MsgIntent::class.java)
        }

        return super.onInit()
    }

    override fun onHook() {

        cotr.hookAfter(this) { param ->
            val contact = PluginViewLoader.currentContact
            val seq = seqMap[contact.peerUid]
            if (contact.chatType != 2 || seq == null) return@hookAfter

            ObjectStore.save("data", seqKey, seqMap, seqSerializer)

            if (isFromHistory()) return@hookAfter

            messenger = param.thisObject

            if (config.manualJump) {
                pendingSeq = seq
                showChip(contact.peerUid)
            } else {
                navigate(param.thisObject, seq, 150L)
            }
        }

        AIODelegate::class.java.getDeclaredMethod("hide").hookAfter(this) {
            dismissChip()
        }
    }

    private fun loadSeqMap() {
        val legacy = File("${QQCurrentEnv.currentDir}data", name)
        val old = ObjectStore.load(legacy, seqSerializer)
        if (old != null) {
            seqMap = old.toMutableMap()
            ObjectStore.save("data", seqKey, seqMap, seqSerializer)
            legacy.delete()
            return
        }
        ObjectStore.load("data", seqKey, seqSerializer)?.let { seqMap = it.toMutableMap() }
    }

    private fun navigate(target: Any, seq: Long, delay: Long) {
        val intent = $$"com.tencent.mobileqq.aio.event.MsgNavigationEvent$NavigateBySeqEvent"
            .toClass.newInstanceWithArgs("", seq, 0L, false, null, false, false, null, 228, null)

        if (delay <= 0) {
            if (isFromHistory()) return
            runCatching { sendIntent.invoke(target, intent) }
            return
        }

        ModuleScope.launchMainDelayed(delay) {
            if (isFromHistory()) return@launchMainDelayed
            runCatching { sendIntent.invoke(target, intent) }
        }
    }

    private fun showChip(peerUid: String, retry: Int = 0) {
        val activity = QQCurrentEnv.activity
        if (activity == null) {
            if (retry < 5) ModuleScope.launchMainDelayed(200) { showChip(peerUid, retry + 1) }
            return
        }
        dismissChip()
        chip = RememberLastReadChip(activity) { jumpToLast(peerUid) }.also { it.show() }
    }

    private fun jumpToLast(peerUid: String) {
        val target = messenger ?: return
        val seq = pendingSeq ?: seqMap[peerUid] ?: return
        navigate(target, seq, 0L)
    }

    private fun dismissChip() {
        chip?.dismiss()
        chip = null
    }

    private fun isFromHistory(): Boolean {
        val intent = QQCurrentEnv.activity?.intent ?: return false
        return intent.getStringExtra("preAct") == "NTChatHistoryActivity"
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        RememberLastReadPage(config, ::updateConfig, onDismiss)
    }

}
