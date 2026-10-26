package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class CrashConfig(
    val foregroundDialog: Boolean = true,
    val backgroundSilent: Boolean = true,
    val mainProcessOnly: Boolean = true,
    val childThreadSilent: Boolean = false
)