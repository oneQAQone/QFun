package me.yxp.qfun.utils.dexkit

import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tencent.mobileqq.activity.SplashActivity
import kotlinx.coroutines.delay
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.generated.HookRegistry
import me.yxp.qfun.ui.components.dialogs.CenterDialogContainerNoButton
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.TAG
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseQuery
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

                startFind()
            }
    }

    private fun startFind() {
        ModuleScope.launchIO(TAG) {
            val tasks = HookRegistry.hookItems.filterIsInstance<DexKitTask>().toMutableList()
            tasks.apply {
                add(TroopTool)
                add(MsgTool)
            }

            val sourceDir = HostInfo.hostContext.applicationInfo.sourceDir
            DexKitBridge.create(sourceDir).use { bridge ->
                tasks.forEach { task ->
                    runCatching {
                        task.getQueryMap().forEach { (name, query) ->
                            when (query) {
                                is FindClass -> bridge.findClass(query).singleOrNull()?.let {
                                    val tip = "${task.TAG}->$name"
                                    progressText = tip
                                    DexKitCache.cacheMap[tip] = it.descriptor
                                }

                                is FindMethod -> bridge.findMethod(query).singleOrNull()?.let {
                                    val tip = "${task.TAG}->$name"
                                    progressText = tip
                                    DexKitCache.cacheMap[tip] = it.descriptor
                                }
                            }
                        }
                    }.onFailure { LogUtils.e(task.TAG, it) }
                }
            }
            progressText = "查找完成，保存并关闭应用"
            DexKitCache.saveCache()
            delay(500)
            Process.killProcess(Process.myPid())
        }
    }
}

interface DexKitTask {

    fun getQueryMap(): Map<String, BaseQuery>

    fun requireClass(name: String): Class<*> {
        return DexKitCache.getClass("${TAG}->$name")
    }

    fun requireMethod(name: String): Method {
        return DexKitCache.getMethod("${TAG}->$name")
    }
}
