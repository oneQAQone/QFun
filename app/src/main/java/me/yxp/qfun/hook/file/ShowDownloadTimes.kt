package me.yxp.qfun.hook.file

import android.annotation.SuppressLint
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import com.tencent.mobileqq.troop.file.data.TroopFileShowAdapter
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.toJson
import me.yxp.qfun.utils.json.walk
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethodOrNull
import me.yxp.qfun.utils.reflect.getObjectByType
import me.yxp.qfun.utils.reflect.setObjectByType
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

@HookItemAnnotation(
    "显示文件下载次数",
    "群文件显示具体下载次数",
    HookCategory.FILE
)
object ShowDownloadTimes : BaseSwitchHookItem(), DexKitTask {

    private var getView: Method? = null

    private var putValue: Method? = null

    private var getStatus: Method? = null

    private const val ADAPTER_CLASS_NAME = "com.tencent.mobileqq.troop.data.TroopFileShowAdapter"

    private val downloadCountMap = ConcurrentHashMap<String, Int>()

    private val tempIdMap = WeakHashMap<Any, String>()

    override fun onInit(): Boolean {

        if (!isInTargetProcess()) return false

        runCatching {
            TroopFileShowAdapter::class.java
        }.recoverCatching {
            ClassUtils.loadFromPlugin("troop_plugin.apk", ADAPTER_CLASS_NAME)
        }.onSuccess {
            getView = it.findMethod {
                name = "getView"
            }
        }

        putValue = runCatching {
            requireClass("ProtoModel").findMethod {
                returnType = void
                paramTypes(int, obj)
            }
        }.getOrNull()

        getStatus = runCatching {
            requireClass("QQFileCell").let {
                it.findMethodOrNull {
                    paramTypes(null, boolean, null)
                } ?: it.findMethod {
                    paramTypes(null, boolean)
                }
            }
        }.getOrNull()

        return getView != null || (getStatus != null && putValue != null)
    }


    @SuppressLint("SetTextI18n")
    override fun onHook() {

        getView?.hookAfter(this) { param ->

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

        putValue?.hookAfter(this) { param ->

            val instance = param.thisObject
            val index = param.args[0]
            val value = param.args[1] ?: return@hookAfter

            if (index == 1) {
                val fileId = value as String
                tempIdMap[instance] = fileId
            } else if (index == 9) {
                val count = value as Int
                val fileId = tempIdMap.remove(instance)
                if (fileId != null) {
                    downloadCountMap[fileId] = count
                }
            }
        }

        getStatus?.hookAfter(this) { param ->

            val fileModel = param.args[0] ?: return@hookAfter
            val fileId = fileModel.toJson().walk("1", "1").str
            val count = downloadCountMap[fileId] ?: return@hookAfter
            val result = param.result ?: return@hookAfter

            val status = result.getObjectByType<String>()
            result.setObjectByType("$status · $count 次")

        }

    }

    private fun extract(source: String, regex: String): String? {
        val matcher = Pattern.compile(regex).matcher(source)
        return if (matcher.find()) matcher.group(1) else null
    }

    override fun getQueryMap(): Map<String, BaseMatcher> = mapOf(
        "ProtoModel" to FindClass().apply {
            matcher {
                usingEqStrings("u", "v")
                methods {
                    add { usingNumbers(4194303) }
                    add {
                        returnType(Map::class.java)
                        usingNumbers((1..17) + (20..24))
                    }
                }
            }
        },

        "QQFileCell" to FindClass().apply {
            searchPackages("qqfile_common.components")
            matcher {
                usingEqStrings("QQFileChooserRedesign_QQFileCell", "feedback_error")
            }
        }
    )
}