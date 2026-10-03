package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class TroopManageConfig(
    val doubleTap: Boolean = false
)
