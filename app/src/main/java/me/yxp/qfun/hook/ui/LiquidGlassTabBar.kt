package me.yxp.qfun.hook.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ancestors
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.tencent.mobileqq.activity.SplashActivity
import com.tencent.mobileqq.app.BaseActivity
import com.tencent.mobileqq.app.FrameFragment
import com.tencent.mobileqq.quibadge.QUIBadge
import com.tencent.mobileqq.tab.TabFrameLayout
import com.tencent.mobileqq.widget.QQTabLayout
import com.tencent.qui.quiblurview.QQBlurViewWrapper
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObjectByType
import me.yxp.qfun.utils.reflect.getObjectOrNull
import me.yxp.qfun.utils.reflect.setObjectByType
import me.yxp.qfun.utils.reflect.toClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

@HookItemAnnotation(
    "液态玻璃导航栏",
    "用 Compose 液态玻璃导航栏替换 QQ 原生底部导航栏",
    HookCategory.OTHER
)
object LiquidGlassTabBar : BaseSwitchHookItem(), DexKitTask {

    override val isNeedRestart = true

    private const val VIEW_TAG = "QFun_LiquidGlassTabBar"

    private const val BADGE_NUM_RED = 2
    private const val BADGE_TEXT_RED = 4

    private var lifecycleOwner: SplashLifecycleOwner? = null
    private var nativeTabLayout: QQTabLayout? = null
    private val tabTags = mutableStateListOf<String>()
    private val currentTabTag = mutableStateOf("")
    private val tabBadgeTexts = mutableStateMapOf<Int, String>()

    private val lifecycleEvents = mapOf(
        "doOnCreate" to Lifecycle.Event.ON_CREATE,
        "doOnStart" to Lifecycle.Event.ON_START,
        "doOnResume" to Lifecycle.Event.ON_RESUME,
        "doOnPause" to Lifecycle.Event.ON_PAUSE,
        "doOnStop" to Lifecycle.Event.ON_STOP,
        "doOnDestroy" to Lifecycle.Event.ON_DESTROY,
    )

    private lateinit var tabRebuildMethod: Method
    private lateinit var onTabChangedMethod: Method
    private lateinit var initTabLayoutSwitch: Method
    private lateinit var needShowTabHostDivider: Method

    private val lifecycleMethods = mutableListOf<Pair<Method, Lifecycle.Event>>()

    override fun onInit(): Boolean {
        tabRebuildMethod = TabFrameLayout::class.java.findMethod {
            returnType = void
            paramTypes(int)
        }
        onTabChangedMethod = FrameFragment::class.java
            .getDeclaredMethod("onTabChanged", String::class.java)

        val baseActivityClass = BaseActivity::class.java
        lifecycleEvents.forEach { (methodName, event) ->
            lifecycleMethods += baseActivityClass.findMethod { name = methodName } to event
        }

        needShowTabHostDivider = requireMethod("needShowTabHostDivider")
        initTabLayoutSwitch = requireMethod("initTabLayoutSwitch")
        return super.onInit()
    }

    override fun onHook() {
        hookLifecycle()
        hookTabRebuild()
        hookTabChanged()
        hookQuiBadge()
        needShowTabHostDivider.returnConstant(this, false)
        initTabLayoutSwitch.hookBefore(this) {
            it.thisObject.setObjectByType(true)
        }
    }

    private fun hookLifecycle() {
        lifecycleMethods.forEach { (hookMethod, event) ->
            hookMethod.hookAfter(this) { param ->
                val activity = param.thisObject as? SplashActivity ?: return@hookAfter

                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        nativeTabLayout = null
                        tabTags.clear()
                        tabBadgeTexts.clear()
                        currentTabTag.value = ""
                        lifecycleOwner = SplashLifecycleOwner(activity).also {
                            it.handle(Lifecycle.Event.ON_CREATE)
                        }
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        lifecycleOwner?.handle(Lifecycle.Event.ON_DESTROY)
                        lifecycleOwner = null
                        nativeTabLayout = null
                        tabTags.clear()
                        tabBadgeTexts.clear()
                    }

                    else -> lifecycleOwner?.handle(event)
                }
            }
        }
    }

    private fun hookTabRebuild() {
        tabRebuildMethod.hookAfter(this) { param ->
            val tabFrameLayout = param.thisObject as TabFrameLayout
            val owner = lifecycleOwner ?: return@hookAfter

            val tabLayout = tabFrameLayout.getObjectByType<QQTabLayout>()
            nativeTabLayout = tabLayout

            hideView(tabLayout)
            val dragFrameLayout = tabLayout.parent as ViewGroup
            val pageRootView = tabFrameLayout.parent.parent as ViewGroup

            dragFrameLayout.children
                .filterIsInstance<QQBlurViewWrapper>()
                .forEach { hideView(it) }

            val viewPagerAdapter = tabFrameLayout.getObjectByType(
                $$"androidx.recyclerview.widget.RecyclerView$Adapter".toClass
            )
            val tabSpecList = viewPagerAdapter.getObjectByType<ArrayList<*>>()

            val tags = tabSpecList.mapNotNull { it.callMethod("getTag") as? String }
            if (tags.isEmpty()) return@hookAfter

            tabTags.clear()
            tabTags.addAll(tags)
            tabBadgeTexts.clear()

            currentTabTag.value = tabLayout.currentTabTag

            ensureComposeView(dragFrameLayout, owner, pageRootView)
        }
    }

    private fun hookTabChanged() {
        onTabChangedMethod.hookAfter(this) { param ->
            currentTabTag.value = param.args[0] as String
        }
    }

    private fun hookQuiBadge() {
        val badgeClass = QUIBadge::class.java
        badgeClass.getDeclaredMethod("setPaintColorAndValidate")
            .hookAfter(this) { syncBadge(it.thisObject as QUIBadge) }
        badgeClass.getDeclaredMethod("setVisibility", Int::class.javaPrimitiveType)
            .hookAfter(this) { syncBadge(it.thisObject as QUIBadge) }
    }

    private fun syncBadge(badge: QUIBadge) {
        val index = badge.findTabIndex() ?: return
        if (!badge.isVisible) {
            tabBadgeTexts.remove(index)
            return
        }
        when (badge.getObjectOrNull("mViewType") as Int) {
            BADGE_NUM_RED -> {
                val num = badge.getObjectOrNull("mNum") as Int
                if (num > 0) tabBadgeTexts[index] = num.toString() else tabBadgeTexts.remove(index)
            }
            BADGE_TEXT_RED -> tabBadgeTexts[index] = badge.getObjectOrNull("mText") as? String ?: ""
            else -> tabBadgeTexts.remove(index)
        }
    }

    private fun QUIBadge.findTabIndex(): Int? {
        val layout = nativeTabLayout ?: return null
        val tabStrip = layout.getChildAt(0) as? ViewGroup ?: return null
        return tabStrip.children.indexOfFirst { tabView -> ancestors.any { it === tabView } }
            .takeIf { it >= 0 }
    }

    private fun hideView(view: View) {
        view.apply {
            isVisible = false
            if (tag != "MARKED") {
                viewTreeObserver.addOnGlobalLayoutListener {
                    if (isVisible) isVisible = false
                }
                tag = "MARKED"
            }
        }
    }

    private fun ensureComposeView(
        realParent: ViewGroup,
        owner: SplashLifecycleOwner,
        pageRootView: ViewGroup
    ) {
        val existingView = realParent.findViewWithTag<ComposeView>(VIEW_TAG)
        if (existingView != null) return

        val composeView = ComposeView(realParent.context).apply {
            tag = VIEW_TAG
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setContent {
                LiquidGlassTabBarContent(
                    tabTags = tabTags,
                    currentTag = currentTabTag.value,
                    badgeTexts = tabBadgeTexts,
                    pageRootView = pageRootView,
                    onTabSelected = { index, tag ->
                        currentTabTag.value = tag
                        nativeTabLayout?.setCurrentTab(index)
                    }
                )
            }
        }

        realParent.addView(
            composeView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        )
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "needShowTabHostDivider" to FindMethod().apply {
            searchPackages("com.tencent.mobileqq.utils")
            matcher {
                usingStrings("needShowTabHostDivider")
            }
        },
        "initTabLayoutSwitch" to FindMethod().apply {
            searchPackages("com.tencent.mobileqq.util")
            matcher {
                usingStrings("initTabLayoutSwitch")
            }
        }
    )

}