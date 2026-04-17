package me.yxp.qfun.hook.msg

import android.widget.TextView
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation(
    "拦截卡屏",
    "屏蔽卡屏消息，请保持开启，否则会将卡屏消息还原",
    HookCategory.MSG
)
object TextViewLongTextFixHook : BaseSwitchHookItem() {

    private const val REPLACEMENT_TEXT = "疑似卡屏，已屏蔽"
    private const val MAX_LENGTH = 7000

    override fun onInit(): Boolean {
        return isInTargetProcess()
    }

    override fun onHook() {
        try {
            TextView::class.java.findMethod {
                name = "setText"
                paramTypes(CharSequence::class.java, TextView.BufferType::class.java, Boolean::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            }.hookBefore(this) { param ->
                fix(param.args[0] as? CharSequence)?.let { param.args[0] = it }
            }

            TextView::class.java.findMethod {
                name = "setText"
                paramTypes(CharSequence::class.java, TextView.BufferType::class.java)
            }.hookBefore(this) { param ->
                fix(param.args[0] as? CharSequence)?.let { param.args[0] = it }
            }

            TextView::class.java.findMethod {
                name = "setText"
                paramTypes(CharSequence::class.java)
            }.hookBefore(this) { param ->
                fix(param.args[0] as? CharSequence)?.let { param.args[0] = it }
            }
        } catch (_: Throwable) {
        }
    }

    private fun fix(text: CharSequence?): String? {
        if (text == null) return null
        if (text.length > MAX_LENGTH) {
            return REPLACEMENT_TEXT
        }
        return null
    }
}
