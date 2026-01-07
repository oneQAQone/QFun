package me.yxp.qfun.plugin.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.yxp.qfun.plugin.loader.PluginManager
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.net.HttpUtils
import me.yxp.qfun.utils.qq.QQCurrentEnv
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ScriptService {

    private const val API_URL = "${HttpUtils.HOST}/api/store.php"

    suspend fun fetchScriptList(): Result<List<ScriptInfo>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = HttpUtils.getSuspend("$API_URL?action=get_uploads")

            if (response.isEmpty()) {
                return@runCatching Result.failure<List<ScriptInfo>>(
                    ScriptException("服务器响应为空")
                )
            }

            val jsonArr = JSONArray(response)
            val list = ArrayList<ScriptInfo>()

            for (i in jsonArr.length() - 1 downTo 0) {
                val item = jsonArr.getJSONObject(i)
                val info = ScriptInfo.parse(item)
                if (info.status == "approved") {
                    list.add(info)
                }
            }

            if (list.isEmpty()) {
                Result.failure(ScriptException("暂无可用脚本"))
            } else {
                Result.success(list)
            }
        }.getOrElse { e ->
            val errorMsg = when {
                e.message?.contains("timeout", true) == true -> "网络超时，请检查网络连接"
                e.message?.contains("connection", true) == true -> "网络连接失败"
                e.message?.contains("json", true) == true -> "数据格式错误"
                else -> "数据解析失败: ${e.message}"
            }
            Result.failure(ScriptException(errorMsg))
        }
    }

    suspend fun downloadAndInstall(script: ScriptInfo): Result<Unit> = withContext(Dispatchers.IO) {
        if (script.filename.isEmpty()) {
            return@withContext Result.failure(ScriptException("文件名无效"))
        }

        val cacheDir = File(QQCurrentEnv.currentDir, "cache").apply { mkdirs() }
        val tempZip = File(cacheDir, "download_${System.currentTimeMillis()}.zip")

        try {
            val downloadSuccess =
                HttpUtils.downloadSuspend(script.downloadUrl, tempZip.absolutePath)
            if (!downloadSuccess) {
                FileUtils.delete(tempZip)
                return@withContext Result.failure(ScriptException("下载请求失败"))
            }

            val errorMsg = PluginManager.installPluginFromZip(tempZip)
            FileUtils.delete(tempZip)

            if (errorMsg == null) {
                Result.success(Unit)
            } else {
                Result.failure(ScriptException(errorMsg))
            }
        } catch (e: Exception) {
            FileUtils.delete(tempZip)
            Result.failure(ScriptException("安装失败: ${e.message}"))
        }
    }

    suspend fun uploadScript(
        id: String,
        author: String,
        desc: String,
        version: String,
        name: String,
        zipFile: File
    ): Result<UploadResult> = withContext(Dispatchers.IO) {
        if (!zipFile.exists() || !zipFile.name.endsWith(".zip", true)) {
            return@withContext Result.failure(ScriptException("无效的脚本文件"))
        }

        val params = mapOf(
            "id" to id,
            "qq" to QQCurrentEnv.currentUin,
            "version" to version,
            "name" to name,
            "description" to desc,
            "author" to author
        )

        try {
            val response = HttpUtils.postMultipartSuspend(API_URL, params, "file", zipFile)
            val json = JSONObject(response)

            if (json.optBoolean("success")) {
                val result = UploadResult(
                    id = json.optString("id") ?: "",
                    status = json.optString("message")
                )
                Result.success(result)
            } else {
                val msg = json.optString("message", "上传失败")
                Result.failure(ScriptException(msg))
            }
        } catch (e: Exception) {
            Result.failure(ScriptException("响应解析错误: ${e.message}"))
        }
    }
}

class ScriptException(message: String) : Exception(message)