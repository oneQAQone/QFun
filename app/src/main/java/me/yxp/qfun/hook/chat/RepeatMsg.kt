package me.yxp.qfun.hook.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.Composable
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msglist.holder.component.msgfollow.AIOMsgFollowComponent
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import me.yxp.qfun.R
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.RepeatConfig
import me.yxp.qfun.hook.api.MenuClickListener
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.ui.pages.configs.RepeatMsgPage
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByType
import me.yxp.qfun.utils.reflect.toClass
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method

@HookItemAnnotation(
    "消息复读",
    "支持快捷图标或长按菜单复读，点击配置具体方式",
    HookCategory.CHAT
)
object RepeatMsg : BaseClickableHookItem<RepeatConfig>(RepeatConfig.serializer()),
    MenuClickListener {

    override val defaultConfig: RepeatConfig = RepeatConfig()

    override val menuKey: String
        get() = if (config.mode == 1) "[QFun],RepeatMsg,复读,," else ""

    private lateinit var handleIntent: Method
    private var lastClickTime = 0L
    var bitmap: Bitmap? = null

    override fun onInit(): Boolean {
        handleIntent = AIOMsgFollowComponent::class.java
            .findMethod {
                returnType = void
                paramTypes(int, AIOMsgItem::class.java.superclass, list)
            }
        return super.onInit()
    }

    override fun onHook() {
        handleIntent.hookAfter(this) { param ->
            val lazy = param.thisObject.getObjectByType("kotlin.Lazy".toClass)
            val repeatView = lazy.callMethod("getValue") as ImageView

            if (config.mode != 0) {
                repeatView.visibility = View.GONE
                return@hookAfter
            }

            val aioMsgItem = param.args[1] as AIOMsgItem
            if (repeatView.context.javaClass.name.contains("MultiForwardActivity")) return@hookAfter

            repeatView.apply {
                visibility = View.VISIBLE
                if (bitmap != null) {
                    setImageBitmap(bitmap)
                } else {
                    setImageResource(R.drawable.ic_action_repeat)
                }
                setOnClickListener {
                    if (!config.doubleClick || isDoubleClick()) {
                        performRepeat(aioMsgItem.msgRecord)
                    }
                }
            }
        }
    }

    override fun onClick(msgData: MsgData) {
        if (config.mode == 1) {
            performRepeat(msgData.data)
        }
    }

    private fun performRepeat(msgRecord: MsgRecord) {
        val msgService = QQCurrentEnv.kernelMsgService
        if (msgService == null) {
            Toasts.toast("获取消息服务失败")
            return
        }

        ModuleScope.launchIO {
            val msgId = msgService.generateMsgUniqueId(
                msgRecord.chatType,
                System.currentTimeMillis()
            )

            msgService.sendMsg(
                msgId, Contact(
                    msgRecord.chatType,
                    msgRecord.peerUid,
                    msgRecord.guildId
                ),
                ArrayList(msgRecord.elements),
                msgRecord.msgAttrs,
                null
            )
        }
    }

    private fun isDoubleClick(): Boolean {
        val now = System.currentTimeMillis()
        val time = now - lastClickTime
        lastClickTime = now
        return time < 500
    }

    override fun initData() {
        val file = File("${QQCurrentEnv.currentDir}data/repeat")
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        }
        super.initData()
    }

    override fun saveData() {
        val file = File("${QQCurrentEnv.currentDir}data/repeat")
        if (FileUtils.ensureFile(file)) {
            FileOutputStream(file).use {
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        super.saveData()
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        RepeatMsgPage(
            currentConfig = config,
            bitmap = bitmap,
            onSave = {
                updateConfig(it)
                Toasts.qqToast(2, "设置已保存，滑动消息列表生效")
            },
            onDismiss = onDismiss
        )
    }
}
