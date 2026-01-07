package me.yxp.qfun.plugin.bean

import com.tencent.mobileqq.data.troop.TroopInfo

data class GroupInfo(
    @JvmField val group: String,
    @JvmField val groupName: String,
    @JvmField val groupOwner: String,
    @JvmField val groupInfo: TroopInfo
)
