package me.yxp.qfun.utils.qq

import com.tencent.mobileqq.pskey.api.IPskeyManager
import me.yxp.qfun.hook.api.OnGetRKey
import me.yxp.qfun.utils.reflect.callMethod
import mqq.manager.TicketManager

@Suppress("DEPRECATION")
object CookieTool {

    private val manager
        get() = QQCurrentEnv.qQAppInterface.getManager(2) as TicketManager

    fun getRealSkey(): String? = manager.getRealSkey(QQCurrentEnv.currentUin)
    fun getSkey(): String? = manager.getSkey(QQCurrentEnv.currentUin)
    fun getStweb(): String? = manager.getStweb(QQCurrentEnv.currentUin)
    fun getPt4Token(url: String): String? = manager.getPt4Token(QQCurrentEnv.currentUin, url)

    @Suppress("UNCHECKED_CAST")
    fun getPskey(url: String): String? {
        val map = runtime<IPskeyManager>()
            .callMethod("getPskeySync", arrayOf(url)) as Map<String, String>
        return map[url]
    }

    fun getFriendRKey() = OnGetRKey.friendRkey

    fun getGroupRKey() = OnGetRKey.groupRkey
    fun getGTK(url: String): String {
        val pskey = getPskey(url)
        return pskey?.let { getBkn(it) }.toString()
    }

    fun getBkn(key: String): Long {
        var hash = 5381
        for (i in key.indices) {
            hash += (hash shl 5) + key[i].code
        }
        return (Int.MAX_VALUE and hash).toLong()
    }

}