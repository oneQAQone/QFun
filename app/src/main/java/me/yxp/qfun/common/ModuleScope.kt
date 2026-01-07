package me.yxp.qfun.common

import android.os.Looper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.yxp.qfun.utils.log.LogUtils
import kotlin.coroutines.CoroutineContext

object ModuleScope : CoroutineScope {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.e("ModuleScope", throwable)
    }

    override val coroutineContext: CoroutineContext =
        SupervisorJob() + Dispatchers.Default + exceptionHandler

    fun launchIO(tag: String = "IO", block: suspend CoroutineScope.() -> Unit) =
        launch(
            Dispatchers.IO + CoroutineExceptionHandler { _, e -> LogUtils.e(tag, e) },
            block = block
        )

    fun launchMain(block: suspend CoroutineScope.() -> Unit) =
        launch(Dispatchers.Main.immediate, block = block)

    fun launchDelayed(delayMillis: Long, block: suspend CoroutineScope.() -> Unit) = launch {
        delay(delayMillis)
        block()
    }

    fun launchMainDelayed(delayMillis: Long, block: suspend CoroutineScope.() -> Unit) =
        launch(Dispatchers.Main) {
            delay(delayMillis)
            block()
        }

    suspend fun <T> onMain(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.Main, block)

    suspend fun <T> onIO(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.IO, block)

    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()
}
