package me.yxp.qfun.conf

import kotlinx.serialization.Serializable

@Serializable
data class AdConfig(
    val blocked: Set<String> = emptySet()
) {
    companion object {
        const val QZONE_FEED = "qzone_feed"
        const val GDT_NATIVE = "gdt_native"
        const val GDT_BANNER = "gdt_banner"
        const val GDT_PRELOAD = "gdt_preload"
        const val LOADING_AD = "loading_ad"
        const val LEBA_SHOPPING = "leba_shopping"
    }
}
