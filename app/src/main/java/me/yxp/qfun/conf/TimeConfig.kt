package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class TimeConfig(
    val format: String = "HH:mm:ss",
    val color: Int = 0xFF0000FF.toInt()
)
