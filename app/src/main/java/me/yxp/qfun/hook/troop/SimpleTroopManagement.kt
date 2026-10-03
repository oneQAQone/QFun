package me.yxp.qfun.hook.troop

import android.app.Activity
import android.os.SystemClock
import android.view.View
import androidx.compose.runtime.Composable
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msglist.holder.component.avatar.AIOAvatarContentComponent
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.TroopManageConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.loader.hookapi.Chain
import me.yxp.qfun.ui.core.compatibility.QFunBottomDialog
import me.yxp.qfun.ui.pages.configs.TroopManagePage
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.getObjectByType
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "简洁群管",
    "点击群聊头像开启群管菜单，省去进入主页管理群员，可配置单击或双击触发",
    HookCategory.GROUP
)
object SimpleTroopManagement :
    BaseClickableHookItem<TroopManageConfig>(TroopManageConfig.serializer()),
    DexKitTask {

    override val defaultConfig = TroopManageConfig()

    private const val DOUBLE_TAP_TIMEOUT = 450L

    private lateinit var onClick: Method

    private var lastClickTime = 0L
    private var pendingSingle: Runnable? = null
    private var pendingView: View? = null

    override fun onInit(): Boolean {
        onClick = requireClass("listener")
            .getDeclaredMethod("onClick", View::class.java)
        return super.onInit()
    }

    override fun onHook() {
        onClick.hookReplace(this) { param ->
            val view = param.args[0] as View
            val listener = param.thisObject

            val component = listener.getObjectByType<AIOAvatarContentComponent>()
            val msgRecord = component.getObjectByType<AIOMsgItem>().msgRecord

            if (msgRecord.chatType != 2) return@hookReplace param.invokeOriginal()

            val troopUin = msgRecord.peerUin.toString()
            val troopInfo = TroopTool.getGroupInfo(troopUin)
            if (!troopInfo.isOwnerOrAdmin) return@hookReplace param.invokeOriginal()

            val activity = view.context as Activity

            if (!config.doubleTap) {
                showManagementSheet(activity, msgRecord, param, troopInfo.isOwner)
                return@hookReplace null
            }

            val now = SystemClock.uptimeMillis()
            if (now - lastClickTime < DOUBLE_TAP_TIMEOUT) {
                lastClickTime = 0L
                pendingSingle?.let { pendingView?.removeCallbacks(it) }
                pendingSingle = null
                pendingView = null
                showManagementSheet(activity, msgRecord, param, troopInfo.isOwner)
                return@hookReplace null
            }

            lastClickTime = now
            val msgId = msgRecord.msgId
            val single = Runnable {
                pendingSingle = null
                pendingView = null
                lastClickTime = 0L
                if (currentMsgId(listener) != msgId) return@Runnable
                runCatching { param.invokeOriginal() }
            }
            pendingSingle = single
            pendingView = view
            view.postDelayed(single, DOUBLE_TAP_TIMEOUT)
            null
        }
    }

    private fun currentMsgId(listener: Any): Any? = runCatching {
        listener.getObjectByType<AIOAvatarContentComponent>()
            .getObjectByType<AIOMsgItem>()
            .msgRecord
            .msgId
    }.getOrNull()

    private fun showManagementSheet(
        activity: Activity,
        msgRecord: MsgRecord,
        param: Chain,
        isOwner: Boolean
    ) {
        val troopUin = msgRecord.peerUin.toString()
        val memberUin = msgRecord.senderUin.toString()
        val nick = msgRecord.sendMemberName.ifEmpty { msgRecord.sendNickName }

        fun dismissAndRun(dismiss: () -> Unit, action: () -> Unit) {
            dismiss()
            runAction(action)
        }

        QFunBottomDialog(activity) { dismiss ->
            TroopManagementContent(
                activity = activity,
                memberUin = memberUin,
                memberNick = nick,
                isOwner = isOwner,
                onEnterProfile = { dismissAndRun(dismiss) { param.invokeOriginal() } },
                onRecall = {
                    dismissAndRun(dismiss) {
                        MsgTool.recallMsg(
                            2,
                            troopUin,
                            msgRecord.msgId
                        )
                    }
                },
                onSetAdmin = {
                    dismissAndRun(dismiss) {
                        TroopTool.setGroupAdmin(
                            troopUin,
                            memberUin,
                            true
                        )
                    }
                },
                onCancelAdmin = {
                    dismissAndRun(dismiss) {
                        TroopTool.setGroupAdmin(
                            troopUin,
                            memberUin,
                            false
                        )
                    }
                },
                onSetMute = { duration ->
                    dismissAndRun(dismiss) {
                        TroopTool.shutUp(
                            troopUin,
                            memberUin,
                            duration
                        )
                    }
                },
                onCancelMute = {
                    dismissAndRun(dismiss) {
                        TroopTool.shutUp(
                            troopUin,
                            memberUin,
                            0
                        )
                    }
                },
                onSetTitle = { title ->
                    dismissAndRun(dismiss) {
                        TroopTool.setGroupMemberTitle(
                            troopUin,
                            memberUin,
                            title
                        )
                    }
                },
                onSetCard = { card ->
                    dismissAndRun(dismiss) {
                        TroopTool.changeMemberName(
                            troopUin,
                            memberUin,
                            card
                        )
                        msgRecord.sendMemberName = card
                    }
                },
                onKick = {
                    dismissAndRun(dismiss) {
                        TroopTool.kickGroup(
                            troopUin,
                            memberUin,
                            false
                        )
                    }
                },
                onKickBlock = {
                    dismissAndRun(dismiss) {
                        TroopTool.kickGroup(
                            troopUin,
                            memberUin,
                            true
                        )
                    }
                },
                onMuteAll = { dismissAndRun(dismiss) { TroopTool.shutUpAll(troopUin, true) } },
                onUnmuteAll = { dismissAndRun(dismiss) { TroopTool.shutUpAll(troopUin, false) } },
                getCurrentCard = { nick }
            )
        }.show()
    }

    private fun runAction(action: () -> Unit) {
        runCatching { action() }.onFailure { LogUtils.e(this, it) }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        TroopManagePage(config, ::updateConfig, onDismiss)
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "listener" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.avatar")
            matcher {
                addInterface(View.OnClickListener::class.java.name)
                methods {
                    add { name("onClick") }
                }
            }
        }
    )
}
