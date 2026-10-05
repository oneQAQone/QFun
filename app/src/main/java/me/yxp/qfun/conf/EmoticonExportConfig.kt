package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class EmoticonExportConfig(
    val selected: Set<String> = emptySet()
)
