package me.yxp.qfun.annotation

object HookCategory {
    const val CHAT = "聊天"
    const val MSG = "消息"
    const val GROUP = "群聊"
    const val PURIFY = "净化"
    const val DEVICE = "设备"
    const val FILE = "文件"
    const val SOCIAL = "社交"
    const val OTHER = "其他"

    val ORDER = listOf(
        CHAT, MSG, GROUP, SOCIAL, PURIFY, DEVICE, FILE, OTHER
    )
}