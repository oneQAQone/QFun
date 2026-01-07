package me.yxp.qfun.hook.api

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.children
import com.tencent.mobileqq.aio.msg.AIOMsgItem
import com.tencent.mobileqq.aio.msg.GrayTipsMsgItem
import com.tencent.mobileqq.aio.msglist.holder.AIOBubbleMsgItemVB
import com.tencent.mvi.base.mvi.MviUIState
import com.tencent.qqnt.aio.holder.template.BubbleLayoutCompatPress
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByType
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull

@HookItemAnnotation("监听消息视图更新")
object OnAIOViewUpdate : BaseApiHookItem<AIOViewUpdateListener>() {

    override fun loadHook() {

        AIOBubbleMsgItemVB::class.java
            .findMethod {
                returnType = void
                paramTypes(MviUIState::class.java)
            }.hookAfter(this) { param ->

                val parent = param.thisObject.getObjectByType<View>() as ViewGroup
                val aioMsgItem = param.args[0]
                    ?.getObjectByTypeOrNull(AIOMsgItem::class.java.superclass) as AIOMsgItem?
                if (aioMsgItem == null || aioMsgItem is GrayTipsMsgItem) return@hookAfter

                val msgRecord = aioMsgItem.msgRecord
                val msgLayout =
                    parent.children.filterIsInstance<BubbleLayoutCompatPress>().singleOrNull()
                        ?: return@hookAfter
                val frameLayout = setupFrameLayout(msgLayout)
                listenerSet.filter { verify(it) }
                    .forEach { it.onUpdate(frameLayout, msgRecord) }


            }
    }

    private fun setupFrameLayout(msgLayout: BubbleLayoutCompatPress): FrameLayout {

        val tag0 = msgLayout.getChildAt(0).tag

        if (tag0 == null || "HOOK" != tag0) {
            val context = msgLayout.context
            val origin = msgLayout.getChildAt(0)
            msgLayout.removeAllViews()
            val frameLayout = FrameLayout(context).apply {
                tag = "HOOK"
                addView(
                    LinearLayout(context).apply {
                        addView(origin)
                    }
                )
            }
            msgLayout.addView(frameLayout)
            return frameLayout

        } else {
            return msgLayout.getChildAt(0) as FrameLayout
        }
    }

}

fun interface AIOViewUpdateListener : Listener {
    fun onUpdate(frameLayout: FrameLayout, msgRecord: MsgRecord)
}