package me.yxp.qfun.hook.file

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
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
import me.yxp.qfun.utils.reflect.getObjectOrNull
import java.io.File
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max

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

    private val thumbCache = java.util.concurrent.ConcurrentHashMap<String, ImageBitmap>()

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

    fun loadEmoticons(): List<Any> {
        val api = apiMethod ?: return emptyList()
        val utilsClass = FAV_UTILS.clazz ?: return emptyList()

        val service = runCatching { api.invoke(null, utilsClass) }.getOrNull() ?: return emptyList()
        val data = runCatching { dataMethod?.invoke(service) }.getOrNull()

        return (data as? List<*>)?.filterNotNull() ?: emptyList()
    }

    suspend fun thumbnail(context: Context, item: Any): ImageBitmap? {
        val id = idOf(item)

        thumbCache[id]?.let { return it }

        val bitmap = withContext(decodeDispatcher) {
            decodeLocal(item) ?: fromDrawable(context, item)
        }
        if (bitmap != null) thumbCache[id] = bitmap
        return bitmap
    }

    private fun decodeLocal(item: Any): ImageBitmap? {
        val path = item.getObjectOrNull("path") as? String ?: return null
        if (path.isBlank()) return null

        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

            val options = BitmapFactory.Options().apply {
                inSampleSize = max(1, minOf(bounds.outWidth, bounds.outHeight) / 96)
            }
            BitmapFactory.decodeFile(path, options)?.asImageBitmap()
        }.getOrNull()
    }

    private fun fromDrawable(context: Context, item: Any): ImageBitmap? {
        val drawable = runCatching {
            item.callMethod("getDrawable", context, 1f) as? Drawable
        }.getOrNull()

        return runCatching { drawable?.toBitmap(128, 128)?.asImageBitmap() }.getOrNull()
    }

    fun idOf(item: Any): String {
        val eId = item.getObjectOrNull("eId") as? String
        if (!eId.isNullOrBlank()) return eId

        val md5 = item.getObjectOrNull("emojiMd5") as? String
        if (!md5.isNullOrBlank()) return md5

        return item.hashCode().toString()
    }

    fun nameOf(item: Any): String {
        val name = runCatching { item.callMethod("getName") as? String }.getOrNull()
        return name?.takeIf { it.isNotBlank() } ?: idOf(item)
    }

    fun export(items: List<Any>) {
        if (items.isEmpty()) {
            Toasts.qqToast(1, "没有可导出的表情")
            return
        }

        ModuleScope.launchIO(name) {
            val dir = File(exportDir).apply { mkdirs() }

            var ok = 0
            val pending = mutableListOf<Any>()

            items.forEach { item ->
                if (copyLocal(item, dir)) ok++ else pending += item
            }

            val failures = mutableListOf<String>()

            coroutineScope {
                pending.map { item ->
                    async(decodeDispatcher) { item to download(item, dir) }
                }.awaitAll().forEach { (item, error) ->
                    if (error == null) ok++ else failures += "${idOf(item)} -> $error"
                }
            }

            if (failures.isNotEmpty()) {
                LogUtils.e(
                    this@EmoticonExport,
                    IllegalStateException(
                        "收藏表情导出失败 ${failures.size} 个：\n" + failures.take(8).joinToString("\n")
                    )
                )
            }

            Toasts.qqToast(
                if (failures.isEmpty()) 2 else 1,
                "导出完成：成功 $ok 个，失败 ${failures.size} 个\n$exportDir"
            )
        }
    }

    private fun copyLocal(item: Any, dir: File): Boolean {
        val path = item.getObjectOrNull("path") as? String
        if (path.isNullOrBlank()) return false

        val source = File(path)
        if (!source.exists() || source.length() == 0L) return false

        return runCatching {
            source.copyTo(File(dir, fileNameOf(item, path)), overwrite = true)
            true
        }.getOrDefault(false)
    }

    private fun download(item: Any, dir: File): String? {
        val url = item.getObjectOrNull("url") as? String
        if (url.isNullOrBlank()) return "本地文件不存在且没有 url"

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
        return "${idOf(item)}.$ext"
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        EmoticonExportPage(config, ::updateConfig, onDismiss)
    }

}
