package me.yxp.qfun.ui.core.compatibility

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.ui.ThemeHelper

@Suppress("DEPRECATION")
abstract class XposedComposeDialog(
    context: Context
) : Dialog(context, android.R.style.Theme_Material_Light_NoActionBar) {

    private val dialogLifecycleOwner = XposedDialogLifecycleOwner()
    protected var isVisible by mutableStateOf(true)
    private var composeView: ComposeView? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(true)
        configureWindow()
    }

    protected open fun configureWindow() {
        window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.CENTER)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.5f)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(dialogLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(dialogLifecycleOwner)
        }

        setContentView(composeView!!)

        window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(dialogLifecycleOwner)
            decorView.setViewTreeSavedStateRegistryOwner(dialogLifecycleOwner)
        }

        composeView?.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides dialogLifecycleOwner) {
                QFunTheme(ThemeHelper.isNightMode()) {
                    DialogContent()
                }
            }
        }
    }

    @Composable
    protected abstract fun DialogContent()

    protected fun dismissWithAnimation() {
        isVisible = false
        window?.decorView?.postDelayed({
            dismiss()
        }, 200)
    }

    override fun onStart() {
        super.onStart()
        dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onStop() {
        dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun dismiss() {
        try {
            dialogLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            composeView = null
        } catch (_: Exception) {
        }
        super.dismiss()
    }
}

class XposedDialogLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    init {
        savedStateRegistryController.performRestore(null)
    }

    val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
}

class QFunCenterDialog(
    context: Context,
    private val content: @Composable (onDismiss: () -> Unit) -> Unit
) : XposedComposeDialog(context) {

    @Composable
    override fun DialogContent() {
        QFunCenterDialogWrapper(
            visible = isVisible,
            onDismiss = ::dismissWithAnimation,
            content = { content(::dismissWithAnimation) }
        )
    }
}

@Composable
private fun QFunCenterDialogWrapper(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(tween(200), initialScale = 0.9f) + fadeIn(tween(200)),
                exit = scaleOut(tween(150), targetScale = 0.9f) + fadeOut(tween(150))
            ) {
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                ) {
                    content()
                }
            }
        }
    }
}

class QFunBottomDialog(
    context: Context,
    private val content: @Composable (onDismiss: () -> Unit) -> Unit
) : XposedComposeDialog(context) {

    override fun configureWindow() {
        super.configureWindow()
        window?.setGravity(Gravity.BOTTOM)
    }

    @Composable
    override fun DialogContent() {
        QFunBottomDialogWrapper(
            visible = isVisible,
            onDismiss = ::dismissWithAnimation,
            content = { content(::dismissWithAnimation) }
        )
    }
}

@Composable
private fun QFunBottomDialogWrapper(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = QFunTheme.colors

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(tween(300)) { it } + fadeIn(tween(200)),
                exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(150))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(colors.background)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                ) {
                    content()
                }
            }
        }
    }
}
