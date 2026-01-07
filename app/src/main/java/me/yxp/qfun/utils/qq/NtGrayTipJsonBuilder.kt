package me.yxp.qfun.utils.qq

import org.json.JSONArray
import org.json.JSONObject

class NtGrayTipJsonBuilder {

    private val items = ArrayList<Item>(4)

    fun appendText(text: String): NtGrayTipJsonBuilder {
        items.add(TextItem(text))
        return this
    }

    fun append(item: Item): NtGrayTipJsonBuilder {
        items.add(item)
        return this
    }

    fun build(): JSONObject {
        return JSONObject().apply {
            put("align", "center")
            val jsonItems = JSONArray()
            items.forEach {
                jsonItems.put(it.toJson())
            }
            put("items", jsonItems)
        }
    }

    interface Item {
        fun toJson(): JSONObject
    }

    class TextItem(private val text: String) : Item {
        override fun toJson(): JSONObject {
            return JSONObject().apply {
                put("txt", text)
                put("type", "nor")
            }
        }

        override fun toString(): String = toJson().toString()
    }

    class UserItem(
        private val uin: String,
        private val uid: String,
        private val nick: String
    ) : Item {
        override fun toJson(): JSONObject {
            return JSONObject().apply {
                put("col", "3")
                put("jp", uid)
                put("nm", nick)
                put("tp", "0")
                put("type", "qq")
                put("uid", uid)
                put("uin", uin)
            }
        }

        override fun toString(): String = toJson().toString()
    }

    class MsgRefItem(
        private val text: String,
        private val msgSeq: Long
    ) : Item {
        constructor(text: String, msgSeq: Int) : this(text, msgSeq.toLong())

        override fun toJson(): JSONObject {
            return JSONObject().apply {
                put("type", "url")
                put("txt", text)
                put("col", "3")
                put("local_jp", 58)
                put("param", JSONObject().apply {
                    put("seq", msgSeq)
                })
            }
        }

        override fun toString(): String = toJson().toString()
    }

    companion object {
        const val AIO_AV_C2C_NOTICE = 2021L
        const val AIO_AV_GROUP_NOTICE = 2022L
    }
}