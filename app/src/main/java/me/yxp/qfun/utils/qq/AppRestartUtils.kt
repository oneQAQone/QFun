package me.yxp.qfun.utils.qq

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.Process
import android.view.Window
import androidx.core.graphics.createBitmap
import me.yxp.qfun.common.ModuleScope
import kotlin.system.exitProcess

object AppRestartUtils {

    private const val QQ_RESTART_ACTIVITY = "com.tencent.mobileqq.login.restart.MainProcessRestartLoadingActivity"

    fun restartApp(context: Context = QQCurrentEnv.activity ?: HostInfo.hostContext, tipText: String = "重启中...") {

        val activity = context as? Activity
        killSubProcesses(context)
        val screenshot = activity?.window?.let { captureScreenshot(it) }
        if (!tryNativeRestart(context, screenshot, tipText)) {
            normalRestart(context)
        }
    }

    private fun tryNativeRestart(context: Context, screenshot: Bitmap?, tipText: String): Boolean {
        return try {
            val intent = Intent().apply {
                component = ComponentName(HostInfo.packageName, QQ_RESTART_ACTIVITY)
                if (screenshot != null) {
                    val bundle = Bundle().apply {
                        putBinder("loadingBg", MockRestartBinder(screenshot))
                    }
                    putExtras(bundle)
                }
                if (tipText.isNotEmpty()) {
                    putExtra("tipText", tipText)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun normalRestart(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(HostInfo.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        ModuleScope.launchDelayed(200) {
            exitProcess(0)
        }
    }

    fun killSubProcesses(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = am.runningAppProcesses ?: return
        val myPid = Process.myPid()
        val myUid = Process.myUid()
        val packageName = HostInfo.packageName
        for (processInfo in runningAppProcesses) {
            if (processInfo.uid == myUid &&
                processInfo.pid != myPid &&
                processInfo.processName != packageName
            ) {
                Process.killProcess(processInfo.pid)
            }
        }
    }

    private fun captureScreenshot(window: Window): Bitmap? {
        val decorView = window.decorView
        if (decorView.width <= 0 || decorView.height <= 0) return null
        return try {
            val bitmap = createBitmap(decorView.width, decorView.height)
            decorView.draw(Canvas(bitmap))
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    private class MockRestartBinder(private val bitmap: Bitmap) : Binder(), IInterface {
        override fun asBinder(): IBinder = this
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == FIRST_CALL_TRANSACTION) {
                try {
                    data.apply {
                        readInt()
                        readString()
                    }
                    reply?.apply {
                        writeNoException()
                        writeInt(1)
                        bitmap.writeToParcel(this, 0)
                    }
                    return true
                } catch (_: Exception) {
                    return false
                }
            }
            return super.onTransact(code, data, reply, flags)
        }
    }
}