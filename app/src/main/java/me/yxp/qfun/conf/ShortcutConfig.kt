package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class ShortcutConfig(
    val visibleIds: Set<Int> = setOf(1000, 1001, 1003, 1004, 1005, 1006)
)