package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class DrawerConfig(
    val hiddenIds: Set<String> = emptySet()
)
