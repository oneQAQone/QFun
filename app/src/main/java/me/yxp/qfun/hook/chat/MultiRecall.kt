package me.yxp.qfun.hook.chat

import android.view.View
import android.widget.LinearLayout
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mvi.mvvm.BaseVM
import com.tencent.mvi.mvvm.framework.FrameworkVM
import me.yxp.qfun.R
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.plugin.bean.MsgData
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObject
import me.yxp.qfun.utils.reflect.newInstanceWithArgs
import me.yxp.qfun.utils.reflect.toClass
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

@HookItemAnnotation(
    "多选撤回",
    "为多选菜单中添加批量撤回功能",
    HookCategory.CHAT
)
object MultiRecall : BaseSwitchHookItem(), DexKitTask {

    private lateinit var makeView: Method
    private lateinit var createVM: Method
    private lateinit var getMsgList: Method
    private lateinit var getMContext: Method
    private lateinit var invoke: Method

    private var multiSelectBarVM: Any? = null

    override fun onInit(): Boolean {

        val className = if (HostInfo.isTIM) {
            "com.tencent.tim.aio.inputbar.TimMultiSelectBarVB"
        } else {
            "com.tencent.mobileqq.aio.input.multiselect.MultiSelectBarVB"
        }

        val innerClassName = $$"$$className$mOperationLayout$2"

        val multiSelectBarVBClass = className.toClass
        val operationLayoutClass = innerClassName.toClass
        val multiForwardClass = requireClass("multiForward")

        getMsgList = multiForwardClass.findMethod {
            returnType = list
            paramCount = 1
        }

        getMContext = FrameworkVM::class.java
            .findMethod {
                returnType = getMsgList.parameterTypes[0].superclass
                paramCount = 0
            }

        invoke = operationLayoutClass.findMethod {
            name = "invoke"
        }

        makeView = multiSelectBarVBClass.findMethod {
            returnType = view
            paramTypes(multiSelectBarVBClass, int, int, View.OnClickListener::class.java)
        }


        createVM = multiSelectBarVBClass.findMethod {
            returnType = BaseVM::class.java
            paramCount = 0
        }
        return super.onInit()
    }

    override fun onHook() {

        createVM.hookAfter(this) {
            multiSelectBarVM = it.result
        }

        invoke.hookAfter(this) { param ->
            val operationLayout = param.result as? LinearLayout ?: return@hookAfter

            val multiSelectBarVB = param.thisObject.getObject("this$0")

            val recallButton = makeView.invoke(
                null,
                multiSelectBarVB,
                R.drawable.ic_action_recall,
                R.drawable.ic_action_recall,
                View.OnClickListener {
                    performBatchRecall()
                }
            ) as View

            val index = (operationLayout.childCount - 2).coerceAtLeast(0)
            operationLayout.addView(recallButton, index)
        }
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun performBatchRecall() {

        try {
            val vm = multiSelectBarVM ?: return
            val multiForwardClass = requireClass("multiForward")

            val multiForwardInstance = multiForwardClass.newInstanceWithArgs()

            val mContext = getMContext.invoke(vm)

            val msgList = getMsgList.invoke(multiForwardInstance, mContext) as List<AIOMsgItem>
            val size = msgList.size
            val newList = CopyOnWriteArrayList<AIOMsgItem>().apply {
                addAll(msgList)
            }
            ModuleScope.launchIO(name) {
                newList.forEach {
                    recallSingleItem(it)
                    if (size > 10) kotlinx.coroutines.delay(300)
                }
            }
            Toasts.qqToast(2, "开始撤回 $size 条消息...")


        } catch (t: Throwable) {
            LogUtils.e(this, t)
            Toasts.qqToast(1, "批量撤回失败: ${t.message}")
        }

        QQCurrentEnv.activity?.onBackPressed()

    }

    private fun recallSingleItem(aioMsgItem: AIOMsgItem) {
        try {

            val msgData = MsgData(aioMsgItem.msgRecord)
            MsgTool.recallMsg(msgData.contact, msgData.msgId)

        } catch (t: Throwable) {
            LogUtils.e(this, t)
        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(

        "multiForward" to FindClass().apply {
            searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.multifoward")
            matcher {
                usingStrings("msgList")
            }
        }
    )
}