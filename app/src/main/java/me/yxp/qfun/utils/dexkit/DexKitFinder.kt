package me.yxp.qfun.utils.dexkit

import android.app.Activity
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
import me.yxp.qfun.ui.components.atoms.DialogButton
import me.yxp.qfun.ui.components.dialogs.BaseDialogSurface
import me.yxp.qfun.ui.core.compatibility.QFunCenterDialog
import me.yxp.qfun.ui.core.theme.QFunTheme
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.MsgTool
import me.yxp.qfun.utils.qq.Toasts
import me.yxp.qfun.utils.qq.TroopTool
import me.yxp.qfun.utils.reflect.TAG
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseMatcher
import java.lang.reflect.Method
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

object DexKitFinder {

    private var titleText by mutableStateOf("查找方法中")
    private var progressText by mutableStateOf("准备开始查找...")
    private var isFinding by mutableStateOf(true)
    private var isRestarting by mutableStateOf(false)

    fun doFind() {
        System.loadLibrary("dexkit")
        showFindDialog()
    }

    @Suppress("DEPRECATION")
    private fun showFindDialog() {
        SplashActivity::class.java
            .getDeclaredMethod("doOnCreate", Bundle::class.java)
            .hookAfter { param ->
                val context = param.thisObject as Context

                QFunCenterDialog(context) { _ ->
                    BaseDialogSurface(
                        title = titleText,
                        bottomBar = {
                            if (isFinding) return@BaseDialogSurface
                            DialogButton(
                                text = "重启应用",
                                onClick = {
                                    if (isRestarting) {
                                        return@DialogButton
                                    }

                                    isRestarting = true
                                    restartApp(context)
                                },
                                isPrimary = true
                            )
                        }
                    ) {
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

    private fun restartApp(context: Context) {
        val activity = context as? Activity
        if (activity == null) {
            Toasts.qqToast(1, "重启失败")

            ModuleScope.launchMain {
                delay(2000.milliseconds)
                Process.killProcess(Process.myPid())
            }
            return
        }

        ModuleScope.launchMain {
            val pm = activity.packageManager
            val intent = pm.getLaunchIntentForPackage(activity.packageName)
            activity.finishAffinity()
            activity.startActivity(intent)
            exitProcess(0)
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
                            val tip = "${task.TAG}->$name"
                            ModuleScope.launchMain {
                                progressText = tip
                            }
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

            DexKitCache.saveCache()
            ModuleScope.launchMain {
                titleText = "查找完成"
                progressText = "查找完成，请点击重启应用"
                isFinding = false
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
