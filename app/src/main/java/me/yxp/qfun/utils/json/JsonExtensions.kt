package me.yxp.qfun.utils.json

import org.json.JSONArray
import org.json.JSONObject

operator fun JSONObject.get(key: String): Any? = if (this.has(key)) this.opt(key) else null

operator fun JSONArray.get(index: Int): Any? = this.opt(index)

val Any?.obj: JSONObject?
    get() = this as? JSONObject

val Any?.arr: JSONArray?
    get() = this as? JSONArray

val Any?.str: String?
    get() = this as? String

val Any?.num: Int?
    get() = (this as? Number)?.toInt()

val Any?.long: Long?
    get() = (this as? Number)?.toLong()

fun JSONObject.walk(vararg path: String): Any? {
    var current: Any? = this
    for (key in path) {
        if (current is JSONObject) {
            current = current.opt(key)
        } else {
            return null
        }
    }
    return current
}

fun Any.findFirstValueByKey(targetKey: String): Any? {

    when (this) {
        is JSONObject -> {

            if (this.has(targetKey)) {
                return this.get(targetKey)
            }

            val keys = this.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val result = this.get(key).findFirstValueByKey(targetKey)
                if (result != null) return result
            }
        }

        is JSONArray -> {

            for (i in 0 until this.length()) {
                val result = this.get(i).findFirstValueByKey(targetKey)
                if (result != null) return result
            }
        }

        is String -> {

            if (this.startsWith("{") || this.startsWith("[")) {
                val parsed = runCatching {
                    if (this.startsWith("{")) JSONObject(this) else JSONArray(this)
                }.getOrNull()
                if (parsed != null) {
                    return parsed.findFirstValueByKey(targetKey)
                }
            }
        }
    }
    return null
}