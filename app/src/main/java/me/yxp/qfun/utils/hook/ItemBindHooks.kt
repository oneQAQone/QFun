package me.yxp.qfun.utils.hook

import android.view.View
import me.yxp.qfun.hook.base.BaseHookItem
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.getObjectOrNull
import java.lang.reflect.Modifier

object ItemBindHooks {

    private const val VIEW_HOLDER_CLASS = "androidx.recyclerview.widget.RecyclerView\$ViewHolder"

    private val BIND_CLASSES = listOf(
        "com.tencent.biz.richframework.part.block.base.BaseListViewAdapter",
        "com.tencent.biz.richframework.part.block.base.PullLoadMoreAdapter",
        "com.tencent.biz.richframework.part.block.BlockMerger",
        "com.qzone.reborn.base.c",
        "com.qzone.reborn.feedpro.block.l",
        "androidx.recyclerview.widget.RecyclerView\$Adapter"
    )

    private val BIND_METHODS = listOf("onBindViewHolder", "bindViewHolder")

    private val hooked = mutableSetOf<String>()

    private val viewHolderClass: Class<*>? by lazy {
        runCatching { ClassUtils.loadClassOrNull(VIEW_HOLDER_CLASS) }.getOrNull()
    }

    fun install(owner: BaseHookItem, onBound: (holder: Any, itemView: View) -> Unit) {
        BIND_CLASSES.forEach { className ->
            var cls = runCatching { ClassUtils.loadClassOrNull(className) }.getOrNull()
            var depth = 0
            while (cls != null && depth < 8) {
                val target = cls
                BIND_METHODS.forEach { methodName ->
                    runCatching {
                        target.findMethods { name = methodName }.forEach { method ->
                            if (Modifier.isAbstract(method.modifiers)) return@forEach
                            val key = "${owner.name}|${target.name}#${method.name}#${method.parameterCount}"
                            if (!hooked.add(key)) return@forEach
                            method.hookAfter(owner) { param ->
                                val holder = param.args.firstOrNull() ?: return@hookAfter
                                val itemView = (holder.getObjectOrNull("itemView", viewHolderClass)
                                    ?: holder.getObjectOrNull("itemView")) as? View ?: return@hookAfter
                                onBound(holder, itemView)
                            }
                        }
                    }.onFailure { LogUtils.e(owner.name, it) }
                }
                cls = target.superclass?.takeIf {
                    it.name.startsWith("com.") || it.name.startsWith("androidx.")
                }
                depth++
            }
        }
    }
}
