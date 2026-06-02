package me.yxp.qfun.utils.json

import me.yxp.qfun.utils.dexkit.DexKitTask
import org.json.JSONObject
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher

object MessageTool : DexKitTask {

    fun encodeToByteArray(message: Any): ByteArray {
        return requireMethod("encode").invoke(null, message) as ByteArray
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "encode" to FindMethod().apply {
            searchPackages("pbandk")
            matcher {
                usingStrings("Unsupported interface")
            }
        }
    )

}

fun Any.toJson(): JSONObject {
    val bytes = MessageTool.encodeToByteArray(this)
    return ProtoData().apply {
        fromBytes(bytes)
    }.toJSON()
}