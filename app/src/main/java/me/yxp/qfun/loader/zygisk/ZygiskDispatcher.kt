package me.yxp.qfun.loader.zygisk

import androidx.annotation.Keep
import me.yxp.qfun.loader.hookapi.Unhook
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * 单方法的调度上下文，直接对接 LSPlant C++ 回调
 */
class ZygiskDispatcher(val target: Member) {

    val isStatic: Boolean = Modifier.isStatic(target.modifiers)

    @Volatile
    lateinit var backupMethod: Method

    class Node(
        val priority: Int,
        val interceptor: (ZygiskChain) -> Any?
    )

    // 不可变列表，支持安全读取快照
    @Volatile
    var interceptors: List<Node> = emptyList()
        private set

    @Synchronized
    fun register(priority: Int, interceptor: (ZygiskChain) -> Any?): Unhook {
        val node = Node(priority, interceptor)
        val newList = ArrayList(interceptors)
        val index = newList.indexOfFirst { it.priority < priority }
        if (index == -1) newList.add(node) else newList.add(index, node)
        interceptors = newList

        return Unhook {
            synchronized(this) {
                val current = ArrayList(interceptors)
                // 仅当节点存在时才更新快照，保证幂等性
                if (current.remove(node)) {
                    interceptors = current
                }
            }
        }
    }

    @Keep
    fun callback(rawArgs: Array<Any?>?): Any? {
        val safeArgs = rawArgs ?: emptyArray()
        val thisObj = if (isStatic || safeArgs.isEmpty()) null else safeArgs[0]
        val actualArgs = when {
            isStatic -> safeArgs
            safeArgs.size > 1 -> safeArgs.copyOfRange(1, safeArgs.size)
            else -> emptyArray()
        }

        // 调用起点截取本次执行的拦截器快照
        val snapshot = interceptors

        if (snapshot.isEmpty()) {
            return callBackup(thisObj, actualArgs)
        }

        return proceed(snapshot, 0, thisObj, actualArgs, Int.MAX_VALUE)
    }

    /**
     * 驱动链条向前执行，通过快照保证单次调用的链条上下文一致
     */
    fun proceed(
        snapshot: List<Node>,
        index: Int,
        thisObj: Any?,
        args: Array<Any?>,
        maxPriority: Int
    ): Any? {
        var cursor = index
        while (cursor < snapshot.size && snapshot[cursor].priority > maxPriority) {
            cursor++
        }

        if (cursor < snapshot.size) {
            val node = snapshot[cursor]
            val chain = ZygiskChain(target, thisObj, args, this, snapshot, cursor, maxPriority)
            return node.interceptor(chain)
        }

        // 到达链条终点，执行底层备份方法
        return callBackup(thisObj, args)
    }

    fun callBackup(thisObj: Any?, args: Array<Any?>): Any? {
        return try {
            backupMethod.invoke(thisObj, *args)
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        }
    }

    companion object {
        val CALLBACK_METHOD: Method by lazy {
            ZygiskDispatcher::class.java.getDeclaredMethod("callback", Array<Any?>::class.java)
        }
    }
}