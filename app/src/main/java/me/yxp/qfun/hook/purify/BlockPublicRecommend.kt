package me.yxp.qfun.hook.purify

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.ItemBindHooks
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.TAG
import me.yxp.qfun.utils.reflect.findMethodOrNull
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.matchers.MethodMatcher
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.WeakHashMap

@HookItemAnnotation(
    "屏蔽空友爱看推荐流",
    "屏蔽好友动态里的空友爱看推荐贴",
    HookCategory.PURIFY
)
object BlockPublicRecommend : BaseSwitchHookItem() {

    override var isEnable: Boolean by BaseSwitchHookItem.BooleanPreference(name, true)

    override val isNeedRestart: Boolean = true

    private const val ITEM_VIEW_BASE_CLASS = "com.qzone.reborn.feedpro.itemview.QzoneBaseFeedProItemView"

    private const val DOMAIN_UTILS_CLASS = "com.qzone.reborn.feedpro.utils.QzoneProPublicDomainCommonUtils"

    private const val FEED_PRO_VIEW_MODEL_CLASS = "com.qzone.reborn.feedpro.viewmodel.QzoneFriendFeedProViewModel"

    private const val BADGE_TEXT = "空友爱看"

    private const val ORIGINAL_HEIGHT_TAG = 0x0F0F0001

    private var itemViewBaseClass: Class<*>? = null

    private val collapsedItems: MutableSet<Any> = Collections.synchronizedSet(
        Collections.newSetFromMap(WeakHashMap<Any, Boolean>())
    )

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        itemViewBaseClass = runCatching { ClassUtils.loadClassOrNull(ITEM_VIEW_BASE_CLASS) }.getOrNull()

        return itemViewBaseClass != null
    }

    override fun onHook() {
        installVisibilityLock()
        ItemBindHooks.install(this) { _, itemView -> onItemBound(itemView) }
        resolveGateAsync()
    }

    private fun resolveGateAsync() {
        ModuleScope.launchIO(TAG) {
            val gate = resolveGate() ?: return@launchIO
            runCatching { gate.hookReplace(this@BlockPublicRecommend) { false } }
                .onFailure { LogUtils.e(this@BlockPublicRecommend, it) }
        }
    }

    private fun resolveGate(): Method? = runCatching {
        System.loadLibrary("dexkit")
        val context = HostInfo.hostContext
        val loader = context.classLoader
        DexKitBridge.create(context.applicationInfo.sourceDir).use { bridge ->
            val utilsCalls = bridge.findMethod(calledBy(DOMAIN_UTILS_CLASS))
                .map { it.descriptor }
                .toHashSet()
            bridge.findMethod(calledBy(FEED_PRO_VIEW_MODEL_CLASS))
                .firstOrNull { it.descriptor in utilsCalls }
                ?.getMethodInstance(loader)
        }
    }.getOrNull()

    private fun calledBy(className: String) = FindMethod().apply {
        matcher {
            returnType = "boolean"
            paramCount = 1
            modifiers = Modifier.STATIC
            callerMethods {
                add(MethodMatcher().apply { declaredClass = className })
            }
        }
    }

    private fun onItemBound(itemView: View) {
        if (isRecoItem(itemView)) {
            collapsedItems.add(itemView)
            collapse(itemView)
        } else if (collapsedItems.remove(itemView)) {
            expand(itemView)
        }
    }

    private fun isRecoItem(itemView: View): Boolean {
        if (itemViewBaseClass?.isInstance(itemView) != true) return false
        return hasExactBadge(itemView)
    }

    private fun hasExactBadge(root: View): Boolean {
        if (root is TextView) return root.text?.toString()?.trim() == BADGE_TEXT
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                if (hasExactBadge(root.getChildAt(i))) return true
            }
        }
        return false
    }

    private fun collapse(view: View) {
        runCatching {
            val params = view.layoutParams ?: return
            if (view.getTag(ORIGINAL_HEIGHT_TAG) == null) {
                view.setTag(ORIGINAL_HEIGHT_TAG, params.height)
            }
            view.visibility = View.GONE
            params.height = 0
            view.requestLayout()
        }.onFailure { LogUtils.e(this, it) }
    }

    private fun expand(view: View) {
        runCatching {
            view.visibility = View.VISIBLE
            val original = view.getTag(ORIGINAL_HEIGHT_TAG)
            if (original is Int) {
                view.layoutParams?.height = original
                view.setTag(ORIGINAL_HEIGHT_TAG, null)
                view.requestLayout()
            }
        }.onFailure { LogUtils.e(this, it) }
    }

    private fun installVisibilityLock(): Boolean = runCatching {
        View::class.java
            .findMethodOrNull {
                name = "setVisibility"
                paramTypes(Int::class.java)
            }
            ?.hookBefore(this) { param ->
                if (collapsedItems.isEmpty()) return@hookBefore
                val view = param.thisObject as? View ?: return@hookBefore
                if (view in collapsedItems && param.args.firstOrNull() != View.GONE) {
                    param.args[0] = View.GONE
                }
            } != null
    }.getOrElse { false }
}
