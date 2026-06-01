package me.yxp.qfun.hook.file

import android.os.Environment
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findField
import me.yxp.qfun.utils.reflect.toClass

@HookItemAnnotation(
    "下载重定向",
    "重定向下载路径到Download/QQ(TIM)",
    HookCategory.FILE,
    "All"
)
object DownloadRedirect : BaseSwitchHookItem() {

    override val isNeedRestart: Boolean = true

    private val redirectPath =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${HostInfo.hostName}/"

    override fun onHook() {
        if (isEnable) {
            "com.tencent.mobileqq.app.AppConstants".toClass
                .findField {
                    name = "SDCARD_FILE_SAVE_PATH"
                }.set(null, redirectPath)
        }
    }

}