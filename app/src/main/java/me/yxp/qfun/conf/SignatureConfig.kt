package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class SignatureConfig(
    val mode: Int = 1,
    val jsonPath: String = "",
    val apiUrl: String = "",
    val jsonKey: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val notifyOnUpdate: Boolean = false,
    val notifyText: String = "已自动更新签名"
)