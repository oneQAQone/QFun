package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class RepeatConfig(
    val enableIcon: Boolean = true,
    val doubleClick: Boolean = false,
    val enableMenu: Boolean = false,
    val enableSwipe: Boolean = false
)