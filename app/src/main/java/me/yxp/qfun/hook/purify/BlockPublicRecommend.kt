package me.yxp.qfun.hook.purify

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.ItemBindHooks
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethodOrNull
import me.yxp.qfun.utils.reflect.getObjectByTypeOrNull
import java.lang.reflect.Method
import java.util.Collections
import java.util.WeakHashMap

private val OBFUSCATED_MODEL = Regex("^[a-z0-9]{1,4}\\.[A-Za-z0-9$]{1,4}$")

@HookItemAnnotation(
    "屏蔽空友爱看推荐流",
    "屏蔽好友动态里的空友爱看推荐贴",
    HookCategory.PURIFY
)
object BlockPublicRecommend : BaseSwitchHookItem() {

    override var isEnable: Boolean by BaseSwitchHookItem.BooleanPreference(name, true)

    override val isNeedRestart: Boolean = true

    private const val ITEM_VIEW_BASE_CLASS = "com.qzone.reborn.feedpro.itemview.QzoneBaseFeedProItemView"

    private val ANCHORS = mapOf(
        15220L to Anchor("eu1.fi", "e", "xw1.b", "Q", "tt1.bw"),
        15730L to Anchor("d31.fl", "e", "y51.c", "Q", "s21.ca"),
        16240L to Anchor("w41.fo", "e", "t71.c", "V", "k41.bz"),
        16410L to Anchor("d51.ft", "e", "b81.c", "W", "r41.bz")
    )

    private class Anchor(
        val gateClass: String,
        val gateMethod: String,
        val predicateClass: String,
        val predicateMethod: String,
        val beanClass: String
    )

    private const val ORIGINAL_HEIGHT_TAG = 0x0F0F0001

    private const val BADGE_TEXT = "空友爱看"

    private var gate: Method? = null
    private var recoPredicate: Method? = null
    private var beanClass: Class<*>? = null
    private var itemViewBaseClass: Class<*>? = null

    private val collapsedItems: MutableSet<Any> = Collections.synchronizedSet(
        Collections.newSetFromMap(WeakHashMap<Any, Boolean>())
    )

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        itemViewBaseClass = runCatching { ClassUtils.loadClassOrNull(ITEM_VIEW_BASE_CLASS) }.getOrNull()

        val anchor = ANCHORS[HostInfo.versionCode]

        beanClass = runCatching { anchor?.beanClass?.clazz }.getOrNull() ?: findBeanClassFromItemView()

        recoPredicate = anchor?.let {
            runCatching {
                it.predicateClass.clazz?.findMethodOrNull {
                    name = it.predicateMethod
                    paramCount = 1
                    returnType = boolean
                }
            }.getOrNull()
        }

        gate = anchor?.let {
            runCatching {
                it.gateClass.clazz?.findMethodOrNull {
                    name = it.gateMethod
                    paramCount = 1
                    returnType = boolean
                }
            }.getOrNull()
        }

        return gate != null || (recoPredicate != null && beanClass != null) || itemViewBaseClass != null
    }

    private fun findBeanClassFromItemView(): Class<*>? {
        var cls: Class<*>? = itemViewBaseClass ?: return null
        var depth = 0
        while (cls != null && depth < 6) {
            cls.declaredFields.forEach { field ->
                val type = field.type
                if (!type.isPrimitive && !type.isArray && !type.isInterface &&
                    OBFUSCATED_MODEL.matches(type.name)
                ) {
                    return type
                }
            }
            cls = cls.superclass
            depth++
        }
        return null
    }

    override fun onHook() {

        gate?.hookReplace(this) { false }

        installItemFilter()
    }

    private fun installItemFilter() {
        installVisibilityLock()
        ItemBindHooks.install(this) { holder, itemView -> onItemBound(holder, itemView) }
    }

    private fun onItemBound(holder: Any, itemView: View) {
        if (isRecoItem(holder, itemView)) {
            collapsedItems.add(itemView)
            collapse(itemView)
        } else if (collapsedItems.remove(itemView)) {
            expand(itemView)
        }
    }

    private fun isRecoItem(holder: Any, itemView: View): Boolean {
        if (itemViewBaseClass?.isInstance(itemView) != true) return false
        val predicate = recoPredicate
        val beanClass = beanClass
        if (predicate == null || beanClass == null) return hasExactBadge(itemView)
        val bean = itemView.getObjectByTypeOrNull(beanClass, itemViewBaseClass)
            ?: holder.getObjectByTypeOrNull(beanClass, itemViewBaseClass)
            ?: return false
        return runCatching { predicate.invoke(null, bean) as? Boolean }.getOrNull() ?: false
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
