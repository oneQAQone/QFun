package me.yxp.qfun.loader.zygisk

import java.lang.reflect.Member
import java.lang.reflect.Method

object ZygiskBridge {

    @JvmStatic
    external fun hookMethod(
        target: Member,
        hooker: Any,
        callback: Method
    ): Method?

    @JvmStatic
    external fun deoptimize(target: Member): Boolean
}