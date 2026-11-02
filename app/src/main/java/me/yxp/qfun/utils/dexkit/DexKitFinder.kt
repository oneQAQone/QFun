package me.yxp.qfun.utils.dexkit

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tencent.mobileqq.activity.SplashActivity
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.generated.HookRegistry
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainerNoButton
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.json.MessageTool
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.AppRestartUtils
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.reflect.TAG
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method

object DexKitFinder {

    private var progressText by mutableStateOf("准备开始查找...")

    fun doFind() {
        System.loadLibrary("dexkit")
        showFindDialog()
    }

    @Suppress("DEPRECATION")
    private fun showFindDialog() {
        SplashActivity::class.java
            .getDeclaredMethod("doOnCreate", Bundle::class.java)
            .hookAfter {
                val context = it.thisObject as Context

                QFunCenterDialog(context) {
                    CenterDialogContainerNoButton(title = "查找方法中") {
                        val colors = QFunTheme.colors
                        Text(
                            text = progressText,
                            fontSize = 15.sp,
                            color = colors.textSecondary,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }.apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    show()
                }

                startFind(context)
            }
    }

    private fun startFind(context: Context) {
        ModuleScope.launchIO(TAG) {
            val tasks = HookRegistry.hookItems.filterIsInstance<DexKitTask>().toMutableList()
            tasks.apply {
                add(MsgTool)
                add(MessageTool)
            }

            val sourceDir = context.applicationInfo.sourceDir
            DexKitBridge.create(sourceDir).use { bridge ->
                tasks.forEach { task ->
                    runCatching {
                        task.getQueryMap().forEach { (name, query) ->
                            val tip = "${task.TAG}->$name"
                            progressText = tip
                            when (query) {
                                is FindClass -> bridge.findClass(query).singleOrNull()?.let {
                                    DexKitCache.cacheMap[tip] = it.descriptor
                                }

                                is FindMethod -> bridge.findMethod(query).singleOrNull()?.let {
                                    DexKitCache.cacheMap[tip] = it.descriptor
                                }
                            }
                        }
                    }.onFailure { LogUtils.e(task.TAG, it) }
                }
            }
            progressText = "查找完成，保存并重启应用"
            DexKitCache.saveCache()
            ModuleScope.launchMain {
                AppRestartUtils.restartApp(context)
            }
        }
    }
}

interface DexKitTask {

    fun getQueryMap(): Map<String, BaseMatcher>

    fun requireClass(name: String): Class<*> {
        return DexKitCache.getClass("${TAG}->$name")
    }

    fun requireMethod(name: String): Method {
        return DexKitCache.getMethod("${TAG}->$name")
    }
}
