package me.yxp.qfun.plugin.bean

data class ForbidInfo(
    @JvmField val user: String,
    @JvmField val endTime: Long,
    @JvmField val time: Long,
    @JvmField val userName: String
)
