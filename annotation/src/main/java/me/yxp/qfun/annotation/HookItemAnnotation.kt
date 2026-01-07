package me.yxp.qfun.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HookItemAnnotation(
    val tag: String,
    val desc: String = "",
    val category: String = HookCategory.OTHER,
    val process: String = ""
)