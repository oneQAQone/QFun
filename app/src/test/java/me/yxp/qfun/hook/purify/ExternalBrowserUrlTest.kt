package me.yxp.qfun.hook.purify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExternalBrowserUrlTest {

    @Test
    fun preservesExternalUrlsAndAddsSchemeToBareDomains() {
        assertEquals("https://example.com/path?q=1#top", externalBrowserUrl("https://example.com/path?q=1#top"))
        assertEquals("https://www.example.com", externalBrowserUrl("www.example.com"))
        assertEquals("ftp://files.example.com/a", externalBrowserUrl("ftp://files.example.com/a"))
    }

    @Test
    fun keepsTencentLoginPaymentMeetingAndCaptchaPagesInternal() {
        listOf(
            "https://qq.com", "https://ti.qq.com/path", "https://QQ.COM:443?foo=bar",
            "https:///ti.qq.com/login", "https://tenpay.com/pay", "https://www.tenpay.com",
            "https://meeting.tencent.com/meeting", "https://sub.meeting.tencent.com",
            "https://qq-web.cdn-go.cn/captcha_cdn-go/latest/captcha.html"
        ).forEach { assertNull(it, externalBrowserUrl(it)) }
    }

    @Test
    fun doesNotTreatUnrelatedDomainsAsTencentPages() {
        listOf("https://notqq.com", "https://qq.com.example.org", "https://nottenpay.com")
            .forEach { assertEquals(it, externalBrowserUrl(it)) }
    }

    @Test
    fun rejectsBlankMalformedAndNonWebUrls() {
        listOf("", " ", "not a url", "https://", "https://exa mple.com", "javascript:alert(1)",
            "file:///sdcard/test.html", "intent://example.com", "mqqapi://example.com")
            .forEach { assertNull(it, externalBrowserUrl(it)) }
    }
}
