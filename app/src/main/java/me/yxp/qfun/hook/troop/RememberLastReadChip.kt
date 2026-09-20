package me.yxp.qfun.hook.troop

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.utils.ui.ThemeHelper

internal class RememberLastReadChip(
    private val activity: Activity,
    private val onTap: () -> Unit
) {

    private var popup: PopupWindow? = null

    fun show() {
        ThemeHelper.applyTheme(activity)
        if (popup != null) return

        val night = ThemeHelper.isNightMode()
        val label = TextView(activity).apply {
            text = "回到上次位置"
            textSize = 13f
            setTextColor(if (night) Color.WHITE else Color.BLACK)
            setPadding(dp(16f), dp(9f), dp(16f), dp(9f))
            background = GradientDrawable().apply {
                cornerRadius = dp(20f).toFloat()
                setColor(if (night) 0xF02B2B2B.toInt() else 0xF0FFFFFF.toInt())
                setStroke(dp(1f), 0x33808080)
            }
            setOnClickListener {
                dismiss()
                onTap()
            }
        }

        popup = PopupWindow(
            label,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        ).apply {
            isOutsideTouchable = false
            isFocusable = false
        }

        ModuleScope.launchMain {
            runCatching {
                popup?.showAtLocation(
                    activity.window.decorView,
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                    0,
                    dp(140f)
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    fun dismiss() {
        ModuleScope.launchMain {
            runCatching { popup?.dismiss() }
            popup = null
        }
    }

    private fun dp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, activity.resources.displayMetrics
    ).toInt()
}
