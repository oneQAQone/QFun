package me.yxp.qfun.hook.file

import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.api.SendMsgListener
import me.yxp.qfun.hook.base.BaseSwitchHookItem

@HookItemAnnotation(
    "上传apk重命名",
    "防止上传apk文件时被添加.1后缀",
    HookCategory.FILE
)
object AutoRemarkApk : BaseSwitchHookItem(), SendMsgListener {

    override fun onSend(elements: ArrayList<MsgElement>) {
        elements.mapNotNull { it.fileElement }
            .filter { it.fileName.endsWith(".apk") }
            .forEach {
                val newName = it.fileName.replace(".apk", ".APK")
                it.fileName = newName

            }
    }
}