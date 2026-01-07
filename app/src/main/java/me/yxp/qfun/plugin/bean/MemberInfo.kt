package me.yxp.qfun.plugin.bean

import com.tencent.mobileqq.data.troop.TroopMemberInfo

data class MemberInfo(
    @JvmField val joinGroupTime: Long,
    @JvmField val lastActiveTime: Long,
    @JvmField val uin: String,
    @JvmField val uinLevel: Int,
    @JvmField val uinName: String,
    @JvmField val role: String,
    @JvmField val memberInfo: TroopMemberInfo
)
