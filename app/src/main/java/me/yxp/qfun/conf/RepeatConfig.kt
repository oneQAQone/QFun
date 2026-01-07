package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class RepeatConfig(
    val mode: Int = 0,
    val doubleClick: Boolean = false
)
