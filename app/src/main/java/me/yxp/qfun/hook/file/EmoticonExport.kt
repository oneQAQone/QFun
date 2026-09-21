package me.yxp.qfun.hook.file

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.conf.EmoticonExportConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.EmoticonExportPage
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.reflect.callMethod
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethodOrNull
import java.io.File
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

data class EmoticonLoadResult(val items: List<Any>, val error: String?)

@HookItemAnnotation(
    "导出收藏表情",
    "浏览并导出 QQ 收藏的自定义表情，支持一键全部导出或勾选导出",
    HookCategory.OTHER
)
object EmoticonExport : BaseClickableHookItem<EmoticonExportConfig>(EmoticonExportConfig.serializer()) {

    override val defaultConfig = EmoticonExportConfig()

    private const val FAV_UTILS = "com.tencent.mobileqq.emoticon.api.IFavEmoticonUtils"

    private const val QROUTE = "com.tencent.mobileqq.qroute.QRoute"

    private val decodeDispatcher = Dispatchers.IO.limitedParallelism(4)

    private val thumbCache = ConcurrentHashMap<String, ImageBitmap>()

    val exportDir: String get() = "${QQCurrentEnv.currentDir}emoticon"

    private var apiMethod: Method? = null
    private var dataMethod: Method? = null

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        apiMethod = QROUTE.clazz?.findMethodOrNull {
            name = "api"
            paramTypes(Class::class.java)
        }
        dataMethod = FAV_UTILS.clazz?.findMethodOrNull {
            name = "getEmoticonData"
            paramCount = 0
        }

        return super.onInit()
    }

    override fun onHook() = Unit

    fun loadEmoticons(): EmoticonLoadResult = try {
        val api = apiMethod ?: error("未找到 $QROUTE.api")
        val utilsClass = FAV_UTILS.clazz ?: error("未找到 $FAV_UTILS")
        val method = dataMethod ?: error("未找到 $FAV_UTILS.getEmoticonData")
        val service = api.invoke(null, utilsClass) ?: error("QRoute.api 返回 null")
        val data = method.invoke(service)

        val items = (data as? List<*>)?.filterNotNull()
            ?: error("返回值不是列表：${data?.javaClass?.name}")

        EmoticonLoadResult(items, null)
    } catch (t: Throwable) {
        EmoticonLoadResult(emptyList(), "${t.javaClass.simpleName}: ${t.message}")
    }

    fun summary(items: List<Any>): String {
        var local = 0
        var remote = 0
        var none = 0

        items.forEach { item ->
            val path = item.field("path")
            val url = item.field("url")
            val file = path?.let { File(it) }

            when {
                file != null && file.exists() && file.length() > 0L -> local++
                !url.isNullOrBlank() -> remote++
                else -> none++
            }
        }

        return "本地已有 $local 个，需要下载 $remote 个，无地址 $none 个"
    }

    suspend fun thumbnail(context: Context, item: Any): ImageBitmap? = try {
        val id = idOf(item)

        thumbCache[id] ?: withContext(decodeDispatcher) {
            decodeLocal(item) ?: fromDrawable(context, item)
        }?.also { thumbCache[id] = it }
    } catch (t: Throwable) {
        report("缩略图", t)
        null
    }

    private fun decodeLocal(item: Any): ImageBitmap? = runCatching {
        val path = item.field("path") ?: return@runCatching null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

        val options = BitmapFactory.Options().apply {
            inSampleSize = max(1, minOf(bounds.outWidth, bounds.outHeight) / 96)
        }
        BitmapFactory.decodeFile(path, options)?.asImageBitmap()
    }.getOrNull()

    private fun fromDrawable(context: Context, item: Any): ImageBitmap? = runCatching {
        val drawable = item.callMethod("getDrawable", context, 1f) as? Drawable
        drawable?.toBitmap(128, 128)?.asImageBitmap()
    }.getOrNull()

    fun idOf(item: Any): String = runCatching {
        item.field("eId") ?: item.field("emojiMd5") ?: item.hashCode().toString()
    }.getOrDefault("unknown")

    internal fun Any.field(name: String): String? = runCatching {
        generateSequence(this::class.java) { it.superclass }
            .mapNotNull { clazz -> runCatching { clazz.getDeclaredField(name) }.getOrNull() }
            .firstOrNull()
            ?.apply { isAccessible = true }
            ?.get(this) as? String
    }.getOrNull()?.trim()?.takeIf { it.isNotEmpty() }?.removePrefix("file://")

    fun export(items: List<Any>) {
        if (items.isEmpty()) {
            toast(1, "没有可导出的表情")
            return
        }

        ModuleScope.launchIO(name) {
            try {
                val dir = File(exportDir).apply { mkdirs() }

                var ok = 0
                val pending = mutableListOf<Any>()

                items.forEach { item ->
                    if (copyLocal(item, dir)) ok++ else pending += item
                }

                val failures = mutableListOf<String>()

                if (pending.isNotEmpty()) {
                    coroutineScope {
                        pending.map { item ->
                            async(decodeDispatcher) { item to download(item, dir) }
                        }.awaitAll().forEach { (item, error) ->
                            if (error == null) ok++ else failures += "${idOf(item)} -> $error"
                        }
                    }
                }

                if (failures.isNotEmpty()) {
                    val detail = failures.take(8).joinToString("\n")
                    LogUtils.d("[收藏表情] 导出失败 ${failures.size} 个：\n$detail")
                    runCatching {
                        LogUtils.e(
                            this@EmoticonExport,
                            IllegalStateException("收藏表情导出失败 ${failures.size} 个：\n$detail")
                        )
                    }
                }

                toast(
                    if (failures.isEmpty()) 2 else 1,
                    "导出完成：成功 $ok 个，失败 ${failures.size} 个\n$exportDir"
                )
            } catch (t: Throwable) {
                report("导出", t)
                toast(1, "导出出错：${t.javaClass.simpleName}: ${t.message.orEmpty().take(80)}")
            }
        }
    }

    private fun copyLocal(item: Any, dir: File): Boolean = runCatching {
        val path = item.field("path") ?: return@runCatching false

        val source = File(path)
        if (!source.exists() || source.length() == 0L) return@runCatching false

        source.copyTo(File(dir, fileNameOf(item, path)), overwrite = true)
        true
    }.getOrDefault(false)

    private fun download(item: Any, dir: File): String? {
        val url = item.field("url") ?: return "本地文件不存在且没有 url"

        val target = File(dir, fileNameOf(item, url))

        val candidates = if (url.startsWith("http://")) {
            listOf(url, "https://" + url.removePrefix("http://"))
        } else {
            listOf(url)
        }

        var last = "未知错误"
        candidates.forEach { candidate ->
            last = downloadTo(candidate, target) ?: return null
        }
        return last
    }

    private fun downloadTo(url: String, target: File): String? {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 20_000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
                setRequestProperty("Referer", "https://qzone.qq.com/")
            }

            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) return "HTTP $code"

            connection.inputStream.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            if (target.length() == 0L) "空文件" else null
        } catch (t: Throwable) {
            "${t.javaClass.simpleName}: ${t.message.orEmpty()}".take(120)
        } finally {
            runCatching { connection?.disconnect() }
        }
    }

    private fun fileNameOf(item: Any, source: String): String {
        val ext = source.substringAfterLast('.', "").takeIf { it.length in 2..5 } ?: "png"
        return "${idOf(item).replace(Regex("[^A-Za-z0-9_-]"), "_")}.$ext"
    }

    private fun toast(icon: Int, message: String) {
        ModuleScope.launchMain { Toasts.qqToast(icon, message) }
    }

    private fun report(title: String, t: Throwable) {
        LogUtils.d("[收藏表情] $title 出错：\n${Log.getStackTraceString(t)}")
        runCatching { LogUtils.e(this, t) }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        EmoticonExportPage(config, ::updateConfig, onDismiss)
    }

}
