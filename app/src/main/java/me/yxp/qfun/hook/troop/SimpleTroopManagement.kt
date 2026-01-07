package me.yxp.qfun.hook.troop

import android.app.Activity
import android.view.View
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msglist.holder.component.avatar.AIOAvatarContentComponent
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.ui.core.compatibility.QFunBottomDialog
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.getObjectByType
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import org.luckypray.dexkit.query.matchers.MethodMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "简洁群管",
    "点击群聊头像开启菜单，省去进入主页管理群员",
    HookCategory.GROUP
)
object SimpleTroopManagement : BaseSwitchHookItem(), DexKitTask {

    lateinit var onClick: Method

    override fun onInit(): Boolean {
        onClick = requireClass("listener")
            .getDeclaredMethod("onClick", View::class.java)
        return super.onInit()
    }

    override fun onHook() {
        onClick.hookReplace(this) { param ->
            val listener = param.thisObject
            val view = param.args[0] as View

            val component = listener.getObjectByType<AIOAvatarContentComponent>()
            val msgItem = component.getObjectByType<AIOMsgItem>()
            val msgRecord = msgItem.msgRecord

            if (msgRecord.chatType != 2) return@hookReplace param.invokeOriginal()

            val troopUin = msgRecord.peerUin.toString()
            val myInfo = TroopTool.getMemberInfo(troopUin, QQCurrentEnv.currentUin)
            if (myInfo.role == "MEMBER") return@hookReplace param.invokeOriginal()

            showManagementSheet(view.context as Activity, msgRecord, param, myInfo.role == "OWNER")
            return@hookReplace null
        }
    }

    private fun showManagementSheet(
        activity: Activity,
        msgRecord: MsgRecord,
        param: XC_MethodHook.MethodHookParam,
        isOwner: Boolean
    ) {
        val troopUin = msgRecord.peerUin.toString()
        val memberUin = msgRecord.senderUin.toString()
        val nick = msgRecord.sendNickName.ifEmpty { msgRecord.sendMemberName }

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
                getCurrentCard = { TroopTool.getMemberInfo(troopUin, memberUin).uinName }
            )
        }.show()
    }

    private fun runAction(action: () -> Unit) {
        runCatching { action() }.onFailure { LogUtils.e(this, it) }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "listener" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.avatar")
            matcher {
                addInterface(View.OnClickListener::class.java.name)
                methods {
                    add(MethodMatcher().name("onClick"))
                }
            }
        }
    )
}
