package me.yxp.qfun.hook.chat

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent
import com.tencent.mobileqq.selectmember.ResultRecord
import com.tencent.qqnt.aio.forward.NtMsgForwardUtils
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObject
import me.yxp.qfun.utils.reflect.getObjectByType
import me.yxp.qfun.utils.reflect.newInstanceWithArgs
import me.yxp.qfun.utils.reflect.setObject
import me.yxp.qfun.utils.reflect.toClass
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

@HookItemAnnotation(
    tag = "转发语音",
    desc = "语音长按菜单出现转发按钮（支持私聊，群聊，临时会话）",
    category = HookCategory.CHAT
)
object ForwardPtt : BaseSwitchHookItem(), DexKitTask {

    private const val MSG_TYPE_PTT = 6
    private const val MSG_TYPE_TEXT = 2
    private const val INVALID_TYPE = 114514

    private lateinit var setMenu: Method
    private lateinit var sendIntent: Method
    private var onActivityResult: Method? = null
    private var handleForward: Method? = null

    private var lastAIOMsgItem: AIOMsgItem? = null
    private lateinit var forwardMenuItem: Class<*>

    override fun onInit(): Boolean {


        setMenu = AIOPttContentComponent::class.java.findMethod {
            returnType = list
            paramCount = 0
        }

        forwardMenuItem = requireClass("ForwardMenuItem")

        val isOldVer = HostInfo.isTIM || (HostInfo.isQQ && HostInfo.versionCode < 11820)

        if (isOldVer) {

            val ntMsgForwardUtils = NtMsgForwardUtils::class.java
            onActivityResult = ntMsgForwardUtils.findMethod {
                returnType = void
                paramTypes(
                    Activity::class.java, int, int, Intent::class.java, null
                )
            }

            sendIntent = ntMsgForwardUtils.findMethod {
                returnType = boolean
                paramTypes(
                    Activity::class.java, null, AIOMsgItem::class.java
                )
            }

        } else {
            val aioForwardHandler =
                "com.tencent.mobileqq.sharepanel.forward.handler.AIOForwardHandler".toClass

            handleForward = aioForwardHandler.findMethod {
                name = "forward"
                paramTypes(map, null, list, string, null)
            }

            sendIntent = requireMethod("sendIntentNew")

        }

        return super.onInit()
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    override fun onHook() {

        setMenu.hookAfter(this) { param ->
            val menu = param.result as MutableList<Any>
            val component = param.thisObject
            val aioMsgItem = component.getObjectByType<AIOMsgItem>(component.javaClass.superclass)
            val activity = QQCurrentEnv.activity ?: return@hookAfter

            val menuItem =
                forwardMenuItem.newInstanceWithArgs(activity, aioMsgItem, component, null)

            menu.add(0, menuItem)

        }

        sendIntent.hookBefore(this) { param ->

            lastAIOMsgItem = null

            val aioMsgItem = param.args[2] as AIOMsgItem
            val msgRecord = aioMsgItem.msgRecord

            if (msgRecord.msgType != MSG_TYPE_PTT) return@hookBefore

            msgRecord.msgType = MSG_TYPE_TEXT
            lastAIOMsgItem = aioMsgItem
        }

        onActivityResult?.hookReplace(this) { param ->
            val intent = param.args[3] as? Intent ?: return@hookReplace param.invokeOriginal()

            val msgId = intent.getLongExtra("forward_nt_msg_id", 0L)
            if (msgId == 0L || lastAIOMsgItem == null) {
                return@hookReplace param.invokeOriginal()
            }

            val contacts = mutableMapOf<String, Int>()

            val elements = lastAIOMsgItem!!.msgRecord.elements

            val uin = intent.getStringExtra("uin")
            val uinType = intent.getIntExtra("uintype", INVALID_TYPE)

            check(uin, uinType, contacts)

            val list = intent.getParcelableArrayListExtra<Parcelable>("forward_multi_target")
            list?.forEach { parcelable ->
                val result = parcelable as ResultRecord
                check(result.uin, result.uinType, contacts)
            }

            if (contacts.isEmpty()) {
                return@hookReplace param.invokeOriginal()
            }
            ModuleScope.launchIO(name) {
                contacts.forEach { (key, value) ->
                    MsgTool.sendMsg(value, key, elements)
                    kotlinx.coroutines.delay(500)
                }
            }
            null

        }

        handleForward?.hookReplace(this) { param ->
            val map = param.args[0] as Map<*, *>
            val list = param.args[2] as List<*>

            if (map.size == 1 && lastAIOMsgItem != null) {
                val msgRecord = lastAIOMsgItem!!.msgRecord
                val elements = msgRecord.elements

                val newList = CopyOnWriteArrayList<Any>().apply {
                    addAll(list)
                }
                ModuleScope.launchIO(name) {
                    newList.forEach { contact ->

                        val peerUin = contact.getObject("peerUin") as String
                        val peerType = contact.getObject("peerType") as Int

                        contact.setObject("peerType", INVALID_TYPE)

                        kotlinx.coroutines.delay(500)
                        try {
                            MsgTool.sendMsg(peerType, peerUin, elements)
                        } catch (t: Throwable) {
                            LogUtils.e(this@ForwardPtt, t)
                        }
                    }
                }
            }
            param.invokeOriginal()
        }
    }

    private fun check(
        uin: String?,
        uinType: Int,
        contacts: MutableMap<String, Int>
    ) {
        val chatType = when (uinType) {
            0 -> 1
            1 -> 2
            1000 -> 100
            else -> INVALID_TYPE
        }
        if (uin != null && chatType != INVALID_TYPE) {
            contacts[uin] = chatType
        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(

        "ForwardMenuItem" to FindClass().apply {
            searchPackages("com.tencent.qqnt.aio.menu")
            matcher {
                usingStrings("ForwardMenuItem")
            }
        },

        "sendIntentNew" to FindMethod().apply {
            matcher {
                declaredClass(NtMsgForwardUtils::class.java)
                returnType(Boolean::class.java)
                usingStrings("startForwardMsgV2 origin_msg_type=")
            }
        }
    )
}