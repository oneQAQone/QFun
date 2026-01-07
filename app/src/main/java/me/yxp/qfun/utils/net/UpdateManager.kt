package me.yxp.qfun.utils.net

import me.yxp.qfun.BuildConfig
import me.yxp.qfun.utils.json.str
import org.json.JSONObject

object UpdateManager {

    data class UpdateInfo(
        val releaseDate: String,
        val latestVersion: String,
        val releaseNotes: String,
        val downloadUrl: String
    )

    suspend fun checkUpdateSuspend(): UpdateInfo? {
        return runCatching {
            val jsonStr = HttpUtils.getSuspend(
                "${HttpUtils.HOST}/api/update.php?version=${BuildConfig.VERSION_NAME}",
            )
            val json = JSONObject(jsonStr)
            val status = json["status"].str ?: ""

            if ("update_available" == status) {
                UpdateInfo(
                    releaseDate = json["release_date"].str ?: "",
                    latestVersion = json["latest_version"].str ?: "",
                    releaseNotes = json["release_notes"].str ?: "",
                    downloadUrl = json["download_url"].str ?: ""
                )
            } else null
        }.getOrNull()
    }
}