package me.yxp.qfun.utils.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object HttpUtils {

    const val HOST = "http://127.0.0.1"
    private const val CONNECT_TIMEOUT = 10000
    private const val READ_TIMEOUT = 15000
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun getSuspend(urlString: String, headers: Map<String, String> = emptyMap()): String =
        withContext(Dispatchers.IO) {
            runCatching { doRequest(urlString, "GET", null, headers) }.getOrDefault("")
        }

    suspend fun postSuspend(
        urlString: String,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): String = withContext(Dispatchers.IO) {
        runCatching { doRequest(urlString, "POST", body, headers) }.getOrDefault("")
    }

    suspend fun postJsonSuspend(urlString: String, json: String): String =
        withContext(Dispatchers.IO) {
            val headers = mapOf("Content-Type" to "application/json; charset=UTF-8")
            runCatching { doRequest(urlString, "POST", json, headers) }.getOrDefault("")
        }

    suspend fun postJsonSuspend(urlString: String, json: JSONObject): String =
        postJsonSuspend(urlString, json.toString())

    suspend fun postFormSuspend(urlString: String, params: Map<String, String>): String =
        withContext(Dispatchers.IO) {
            val body = params.entries.joinToString("&") {
                "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
            }
            val headers = mapOf("Content-Type" to "application/x-www-form-urlencoded")
            runCatching { doRequest(urlString, "POST", body, headers) }.getOrDefault("")
        }

    suspend fun downloadSuspend(urlString: String, savePath: String): Boolean =
        withContext(Dispatchers.IO) {
            downloadSync(urlString, savePath)
        }

    suspend fun postMultipartSuspend(
        urlString: String,
        params: Map<String, String>,
        fileFieldName: String,
        file: File
    ): String = withContext(Dispatchers.IO) {
        runCatching { doMultipartRequest(urlString, params, fileFieldName, file) }
            .getOrDefault("{\"success\":false,\"message\":\"请求失败\"}")
    }

    fun getSync(urlString: String, headers: Map<String, String> = emptyMap()): String =
        runCatching { doRequest(urlString, "GET", null, headers) }.getOrDefault("")

    fun postSync(
        urlString: String,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): String =
        runCatching { doRequest(urlString, "POST", body, headers) }.getOrDefault("")

    fun postJsonSync(urlString: String, json: String): String {
        val headers = mapOf("Content-Type" to "application/json; charset=UTF-8")
        return postSync(urlString, json, headers)
    }

    fun postJsonSync(urlString: String, json: JSONObject): String =
        postJsonSync(urlString, json.toString())

    fun postFormSync(urlString: String, params: Map<String, String>): String {
        val body = params.entries.joinToString("&") {
            "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
        }
        val headers = mapOf("Content-Type" to "application/x-www-form-urlencoded")
        return postSync(urlString, body, headers)
    }

    fun downloadSync(urlString: String, savePath: String): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("User-Agent", USER_AGENT)
                instanceFollowRedirects = true
                doInput = true
            }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val file = File(savePath)
                file.parentFile?.takeIf { !it.exists() }?.mkdirs()
                connection.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        } finally {
            connection?.disconnect()
        }
    }

    fun postMultipartSync(
        urlString: String,
        params: Map<String, String>,
        fileFieldName: String,
        file: File
    ): String = runCatching { doMultipartRequest(urlString, params, fileFieldName, file) }
        .getOrDefault("{\"success\":false,\"message\":\"请求失败\"}")

    private fun doMultipartRequest(
        urlString: String,
        params: Map<String, String>,
        fileFieldName: String,
        file: File
    ): String {
        val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT
            readTimeout = READ_TIMEOUT
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            doInput = true
        }
        connection.outputStream.use { os ->
            params.forEach { (key, value) ->
                os.write("--$boundary\r\n".toByteArray())
                os.write("Content-Disposition: form-data; name=\"$key\"\r\n".toByteArray())
                os.write("\r\n".toByteArray())
                os.write("$value\r\n".toByteArray())
            }
            os.write("--$boundary\r\n".toByteArray())
            os.write("Content-Disposition: form-data; name=\"$fileFieldName\"; filename=\"${file.name}\"\r\n".toByteArray())
            os.write("Content-Type: application/zip\r\n".toByteArray())
            os.write("\r\n".toByteArray())
            file.inputStream().use { fis -> fis.copyTo(os) }
            os.write("\r\n".toByteArray())
            os.write("--$boundary--\r\n".toByteArray())
            os.flush()
        }
        val responseCode = connection.responseCode
        val stream =
            if (responseCode in 200..299) connection.inputStream else connection.errorStream
        return stream?.bufferedReader()?.use { it.readText() }
            ?: "{\"success\":false,\"message\":\"请求失败: $responseCode\"}"
    }

    @Throws(Exception::class)
    private fun doRequest(
        urlString: String,
        method: String,
        body: String?,
        headers: Map<String, String>
    ): String {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("User-Agent", USER_AGENT)
                instanceFollowRedirects = true
                headers.forEach { (k, v) -> setRequestProperty(k, v) }
                if (method == "POST" && body != null) {
                    doOutput = true
                    doInput = true
                    outputStream.use { os ->
                        os.write(body.toByteArray(Charsets.UTF_8))
                        os.flush()
                    }
                }
            }
            val responseCode = connection.responseCode
            val stream =
                if (responseCode in 200..299) connection.inputStream else connection.errorStream
            return stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
        } finally {
            connection?.disconnect()
        }
    }
}
