package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class TroopSetConfig(
    val selectedSet: Set<String> = emptySet()
)
