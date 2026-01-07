package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class DeviceConfig(
    val fakeModel: String = ""
)
