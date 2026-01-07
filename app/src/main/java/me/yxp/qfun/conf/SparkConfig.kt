package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class SparkConfig(
    val contacts: Set<String> = emptySet(),
    val message: String = "续火",
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0
)
