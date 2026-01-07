package me.yxp.qfun.plugin.view

import android.annotation.SuppressLint
import android.app.Activity
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact
import me.yxp.qfun.R
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.plugin.loader.PluginManager
import me.yxp.qfun.ui.core.compatibility.QFunBottomDialog
import me.yxp.qfun.ui.core.theme.AccentGreen
import me.yxp.qfun.ui.core.theme.Dimens
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.log.PluginError
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.ui.ThemeHelper
import kotlin.math.abs

class PluginView(private val activity: Activity) {
    private var popupWindow: PopupWindow? = null
    private var floatBtn: View? = null
    private var lastX = 0
    private var lastY = 0
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var isDragging = false

    companion object {
        private var savedX = -1
        private var savedY = -1
    }

    fun show() {
        ThemeHelper.applyTheme(activity)
        if (popupWindow != null) return

        val currentContact = PluginViewLoader.currentContact
        PluginManager.plugins.filter { it.isRunning }
            .forEach {
                it.compiler.callback.chatInterface(
                    currentContact.chatType,
                    currentContact.peerUin,
                    currentContact.peerName
                )
            }

        val metrics = activity.resources.displayMetrics
        if (savedX == -1 || savedY == -1) {
            lastX = metrics.widthPixels / 2
            lastY = metrics.heightPixels / 2
        } else {
            lastX = savedX
            lastY = savedY
        }

        val size = dp2px(20f)
        val touchSize = dp2px(35f)

        val imageView = ImageView(activity).apply {
            setImageResource(R.drawable.ic_plugin)
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = ViewGroup.LayoutParams(size, size)
        }

        floatBtn = imageView
        popupWindow = PopupWindow(imageView, touchSize, touchSize, false).apply {
            isOutsideTouchable = false
            isFocusable = false
            elevation = 0f
        }

        setupTouchListener()

        ModuleScope.launchMain {
            try {
                ensurePositionInScreen()
                popupWindow?.showAtLocation(
                    activity.window.decorView,
                    Gravity.NO_GRAVITY,
                    lastX,
                    lastY
                )
            } catch (_: Exception) {
            }
        }
    }

    fun dismiss() {
        ModuleScope.launchMain {
            popupWindow?.dismiss()
            popupWindow = null
        }
    }

    private fun ensurePositionInScreen() {
        val metrics = activity.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val offset = dp2px(25f)

        if (lastX < 0) lastX = 0
        if (lastX > width - offset) lastX = width - offset
        if (lastY < 0) lastY = 0
        if (lastY > height - offset) lastY = height - offset
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        floatBtn?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    initialTouchX = lastX
                    initialTouchY = lastY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - touchStartX
                    val dy = event.rawY - touchStartY

                    if (abs(dx) > 10 || abs(dy) > 10) isDragging = true

                    if (isDragging) {
                        lastX = (initialTouchX + dx).toInt()
                        lastY = (initialTouchY + dy).toInt()
                        popupWindow?.update(lastX, lastY, -1, -1)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        savedX = lastX
                        savedY = lastY
                    } else {
                        showMenu()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun showMenu() {
        val menuItems = mutableListOf<PluginMenuItem>()
        val currentContact = PluginViewLoader.currentContact

        PluginManager.plugins.filter { it.isRunning }.forEach { plugin ->
            if (plugin.compiler.menuItems.isNotEmpty()) {
                menuItems.add(PluginMenuItem.Header(plugin.name, plugin.id))
                plugin.compiler.menuItems.forEach { (name, method) ->
                    menuItems.add(
                        PluginMenuItem.Action(
                            name,
                            plugin.id
                        ) {
                            ModuleScope.launchIO {
                                plugin.compiler.callback.invokeMenuItem(
                                    method,
                                    currentContact.chatType,
                                    currentContact.peerUin,
                                    currentContact.peerName,
                                    Contact(
                                        currentContact.chatType,
                                        currentContact.peerUid,
                                        currentContact.guild
                                    )
                                )
                            }
                        })
                }
            }
        }

        QFunBottomDialog(activity) { dismiss ->
            PluginMenuContent(menuItems, dismiss) { pluginId ->
                val plugin = PluginManager.plugins.find { it.id == pluginId }
                if (plugin != null) {
                    Toasts.qqToast(2, "正在重载: ${plugin.name}...")
                    dismiss()
                    ModuleScope.launchIO {
                        try {
                            PluginManager.reloadPlugin(plugin)
                            Toasts.qqToast(2, "${plugin.name} 重载成功")
                        } catch (e: Exception) {
                            PluginError.evalError(e, plugin)
                        }
                    }
                }
            }
        }.show()
    }

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            activity.resources.displayMetrics
        ).toInt()
    }
}

sealed class PluginMenuItem {
    data class Header(val name: String, val pluginId: String) : PluginMenuItem()
    data class Action(val name: String, val pluginId: String, val onClick: () -> Unit) :
        PluginMenuItem()
}

@Composable
private fun PluginMenuContent(
    menuItems: List<PluginMenuItem>,
    onDismiss: () -> Unit,
    onReloadPlugin: (String) -> Unit
) {
    val colors = QFunTheme.colors

    Column(
        Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingLarge)
    ) {
        Text(
            "脚本菜单",
            Modifier.fillMaxWidth(),
            colors.textPrimary,
            22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.PaddingSmall))
        Text(
            "点击脚本名称可重新加载",
            Modifier.fillMaxWidth(),
            colors.textSecondary,
            12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = colors.textSecondary.copy(0.1f))
        Spacer(Modifier.height(20.dp))

        if (menuItems.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp), Alignment.Center
            ) {
                Text("暂无运行中的脚本菜单", fontSize = 14.sp, color = colors.textSecondary)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                items(menuItems) { item ->
                    when (item) {
                        is PluginMenuItem.Header -> PluginHeaderItem(item.name) {
                            onReloadPlugin(
                                item.pluginId
                            )
                        }

                        is PluginMenuItem.Action -> PluginActionItem(item.name) {
                            item.onClick()
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PluginHeaderItem(name: String, onClick: () -> Unit) {
    Text(
        name,
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp, 14.dp),
        AccentGreen,
        16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PluginActionItem(name: String, onClick: () -> Unit) {
    val colors = QFunTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(Dimens.CardCornerRadius, 18.dp),
        Alignment.Center
    ) {
        Text(name, fontSize = 15.sp, color = colors.textPrimary)
    }
}
