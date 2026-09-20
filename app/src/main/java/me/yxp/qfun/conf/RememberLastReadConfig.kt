package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class RememberLastReadConfig(
    val manualJump: Boolean = false
)
