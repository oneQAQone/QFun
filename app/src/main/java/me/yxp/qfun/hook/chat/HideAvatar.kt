package me.yxp.qfun.hook.chat

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.AIOViewUpdateListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.qq.QQCurrentEnv

@HookItemAnnotation(
    "隐藏自身头像",
    "仿TG，聊天时隐藏自身头像",
    HookCategory.CHAT
)
object HideAvatar : BaseSwitchHookItem(), AIOViewUpdateListener {

    private const val MAX_PARENT_DEPTH = 8

    private const val HAS_IMAGE = 1

    private const val HAS_TEXT = 2

    override fun onUpdate(frameLayout: FrameLayout, msgRecord: MsgRecord) {
        val root = frameLayout.itemRoot() ?: return
        val content = root.branchOf(frameLayout) ?: return
        val target = if (isSelf(msgRecord)) View.GONE else View.VISIBLE
        for (index in 0 until root.childCount) {
            val child = root.getChildAt(index)
            if (child === content) continue
            if (!child.isAvatarBranch()) continue
            if (child.visibility != target) child.visibility = target
        }
    }

    private fun isSelf(msgRecord: MsgRecord): Boolean =
        msgRecord.senderUin.toString() == QQCurrentEnv.currentUin

    private fun View.itemRoot(): ViewGroup? {
        var current: View = this
        repeat(MAX_PARENT_DEPTH) {
            val parent = current.parent as? ViewGroup ?: return current as? ViewGroup
            if (parent.javaClass.name.contains("RecyclerView")) return current as? ViewGroup
            current = parent
        }
        return null
    }

    private fun ViewGroup.branchOf(target: View): View? {
        var current: View = target
        while (true) {
            val parent = current.parent as? ViewGroup ?: return null
            if (parent === this) return current
            current = parent
        }
    }

    private fun View.isAvatarBranch(): Boolean = collectFlags() == HAS_IMAGE

    private fun View.collectFlags(): Int {
        var flags = 0
        if (this is ImageView) flags = flags or HAS_IMAGE
        if (this is TextView) flags = flags or HAS_TEXT
        if (flags == (HAS_IMAGE or HAS_TEXT)) return flags
        if (this is ViewGroup) {
            for (index in 0 until childCount) {
                flags = flags or getChildAt(index).collectFlags()
                if (flags == (HAS_IMAGE or HAS_TEXT)) return flags
            }
        }
        return flags
    }
}
