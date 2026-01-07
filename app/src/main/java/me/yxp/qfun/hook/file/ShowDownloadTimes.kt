package me.yxp.qfun.hook.file

import android.annotation.SuppressLint
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import com.tencent.mobileqq.troop.file.data.TroopFileShowAdapter
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.findMethod
import java.lang.reflect.Method
import java.util.regex.Pattern

@HookItemAnnotation(
    "显示文件下载次数",
    "群文件显示具体下载次数",
    HookCategory.FILE
)
object ShowDownloadTimes : BaseSwitchHookItem() {

    private lateinit var getView: Method

    private const val ADAPTER_CLASS_NAME = "com.tencent.mobileqq.troop.data.TroopFileShowAdapter"

    override fun onInit(): Boolean {

        if (!isInTargetProcess()) return false

        val adapter = runCatching {
            TroopFileShowAdapter::class.java
        }.getOrElse {
            ClassUtils.loadFromPlugin("troop_plugin.apk", ADAPTER_CLASS_NAME)
        }
        getView = adapter.findMethod {
            name = "getView"
        }
        return super.onInit()
    }


    @SuppressLint("SetTextI18n")
    override fun onHook() {

        getView.hookAfter(this) { param ->

            val adapter = param.thisObject as BaseAdapter
            val fileItem = adapter.getItem(param.args[0] as Int).toString()

            val fileName = extract(fileItem, "str_file_name='([^']*)'")
            val downloadTimes = extract(fileItem, "uint32_download_times=(\\d+)")

            if (fileName != null && downloadTimes != null && downloadTimes != "0") {
                val view = param.result as View
                val holder = view.tag ?: return@hookAfter

                for (field in holder.javaClass.declaredFields) {
                    field.isAccessible = true
                    val value = field.get(holder)
                    if (value is TextView) {
                        if (value.text.toString() == fileName) {
                            value.text = "(下载: $downloadTimes) $fileName"
                            break
                        }
                    }
                }
            }
        }
    }

    private fun extract(source: String, regex: String): String? {
        val matcher = Pattern.compile(regex).matcher(source)
        return if (matcher.find()) matcher.group(1) else null
    }
}