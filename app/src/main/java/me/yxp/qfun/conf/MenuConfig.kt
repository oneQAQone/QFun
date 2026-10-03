package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class MenuConfig(
    val hiddenKeys: Set<String> = emptySet()
)
