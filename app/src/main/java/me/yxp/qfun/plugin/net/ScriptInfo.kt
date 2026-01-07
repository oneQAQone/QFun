package me.yxp.qfun.plugin.net

import me.yxp.qfun.utils.json.num
import me.yxp.qfun.utils.json.str
import org.json.JSONObject

data class ScriptInfo(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val filename: String,
    val uploadTime: String,
    val status: String,
    val downloads: Int,
    val downloadUrl: String
) {
    companion object {
        fun parse(json: JSONObject): ScriptInfo {
            return ScriptInfo(
                json["id"].str ?: "",
                json["name"].str ?: "Unknown",
                json["version"].str ?: "1.0",
                json["author"].str ?: "Unknown",
                json["description"].str ?: "",
                json["filename"].str ?: "",
                json["date"].str ?: "",
                json["status"].str ?: "unknown",
                json["downloads"].num ?: 0,
                json["download_url"].str ?: ""
            )
        }
    }
}

data class UploadResult(
    val id: String,
    val status: String
)