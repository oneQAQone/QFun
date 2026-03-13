package me.yxp.qfun.utils.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object SocialConfigManager {
    
    private var qqGroupUin: String = ""
    private var telegramChannel: String = ""
    
    var githubRepo: String = "https://github.com/oneQAQone/QFun"
        private set

    val qqGroupUrl: String
        get() = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$qqGroupUin&card_type=group&source=qrcode"

    val telegramUrl: String
        get() = "https://t.me/$telegramChannel"

    suspend fun fetchSocialConfig() {
        withContext(Dispatchers.IO) {
            try {
                val response = HttpUtils.getSuspend("${HttpUtils.HOST}/api/social.php")
                if (response.isNotEmpty()) {
                    val json = JSONObject(response)
                    if (json.has("QQGroup")) {
                        qqGroupUin = json.getString("QQGroup")
                    }
                    if (json.has("Telegram")) {
                        telegramChannel = json.getString("Telegram")
                    }
                }
            } catch (_: Exception) {
            }
        }
    }
}