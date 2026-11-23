package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
enum class OthersJoinMode {
    KEEP_ALL,
    NO_AUTO_EXPAND,
    HIDE_ENTRY
}

@Serializable
data class IceBreakWelcomeConfig(
    val hideSelfWelcomeCard: Boolean = true,
    val othersJoinMode: OthersJoinMode = OthersJoinMode.NO_AUTO_EXPAND
)
