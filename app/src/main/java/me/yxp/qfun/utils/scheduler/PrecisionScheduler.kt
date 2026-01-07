package me.yxp.qfun.utils.scheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.yxp.qfun.common.ModuleScope
import me.yxp.qfun.utils.qq.HostInfo
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

object PrecisionScheduler {

    private const val ACTION_ALARM = "me.yxp.qfun.action.PRECISION_ALARM"
    private const val WAKELOCK_TAG = "QFun:Scheduler"
    private const val PRE_WAKE_SECONDS = 120

    private val context: Context get() = HostInfo.hostContext
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val taskQueue = PriorityBlockingQueue<ScheduledTask>()

    private val isProcessing = AtomicBoolean(false)

    private var processingJob: Job? = null

    var diffTime: Long = 0L
        private set

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_ALARM) {
                handleAlarmTrigger()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun init() {
        try {
            val filter = IntentFilter(ACTION_ALARM)
            if (Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            syncTime()
        } catch (_: Exception) {
        }
    }

    fun currentServerTime(): Long {
        return System.currentTimeMillis() + diffTime
    }

    fun syncTime() {
        ModuleScope.launchIO("TimeSync") {
            try {
                val url = URL("https://www.baidu.com")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 3000
                conn.readTimeout = 3000
                conn.requestMethod = "HEAD"
                conn.instanceFollowRedirects = false

                val startTick = System.currentTimeMillis()
                conn.connect()
                val serverDateHeader = conn.date
                val endTick = System.currentTimeMillis()

                if (serverDateHeader > 0) {
                    val latency = (endTick - startTick) / 2
                    val estimatedServerTime = serverDateHeader + latency
                    diffTime = estimatedServerTime - endTick
                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }
    }

    fun getNextMidnight(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun addTask(task: ScheduledTask) {
        if (task.targetTime < currentServerTime()) {
            return
        }

        taskQueue.removeIf { it.id == task.id }
        taskQueue.add(task)

        val head = taskQueue.peek()
        if (head != null && head.id == task.id && isProcessing.get()) {
            processingJob?.cancel()
        }

        scheduleNextAlarm()
    }

    @SuppressLint("MissingPermission")
    private fun scheduleNextAlarm() {
        val nextTask = taskQueue.peek() ?: return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val targetLocalTime = nextTask.targetTime - diffTime
        val triggerTime = targetLocalTime - (PRE_WAKE_SECONDS * 1000)

        val intent = Intent(ACTION_ALARM)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pi = PendingIntent.getBroadcast(context, 10086, intent, flags)

        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi)
    }

    @Suppress("DEPRECATION")
    private fun handleAlarmTrigger() {
        if (!isProcessing.compareAndSet(false, true)) {
            return
        }

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, WAKELOCK_TAG)
        wakeLock.acquire(10 * 60 * 1000L)

        processingJob = scope.launch {
            try {
                syncTime()

                while (isActive) {
                    val nextTask = taskQueue.peek()

                    if (nextTask == null || (nextTask.targetTime - currentServerTime() > (PRE_WAKE_SECONDS + 10) * 1000)) {
                        break
                    }

                    val targetTime = nextTask.targetTime

                    while (targetTime - currentServerTime() > 2000) {
                        if (taskQueue.peek()?.id != nextTask.id) break
                        delay(100)
                    }

                    if (taskQueue.peek()?.id != nextTask.id) continue

                    @Suppress("ControlFlowWithEmptyBody")
                    while (currentServerTime() < targetTime) {
                    }

                    taskQueue.poll()
                    ModuleScope.launchIO(nextTask.id) {
                        nextTask.action()
                    }
                }
            } finally {
                if (wakeLock.isHeld) wakeLock.release()
                isProcessing.set(false)
                scheduleNextAlarm()
            }
        }
    }
}