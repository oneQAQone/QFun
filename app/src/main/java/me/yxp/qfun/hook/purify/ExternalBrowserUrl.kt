package me.yxp.qfun.hook.purify

import java.net.URI

// Keep host-integrated login, payment, meeting and CAPTCHA pages inside QQ/TIM.
internal fun externalBrowserUrl(url: String): String? {
    val value = url.trim()
    if (value.isEmpty()) return null
    val separator = value.indexOf("://")
    val scheme = if (separator >= 0) value.substring(0, separator).lowercase() else "https"
    if (scheme !in setOf("http", "https", "ftp")) return null
    val body = if (separator >= 0) value.substring(separator + 3).trimStart('/') else value
    val normalized = "$scheme://$body"
    val uri = runCatching { URI(normalized) }.getOrNull() ?: return null
    val host = uri.host?.lowercase()?.trimEnd('.') ?: return null
    if (host.isEmpty() || uri.rawUserInfo != null) return null
    if (listOf("qq.com", "tenpay.com", "meeting.tencent.com").any {
            host == it || host.endsWith(".$it")
        } || host == "qq-web.cdn-go.cn"
    ) return null
    return normalized
}
