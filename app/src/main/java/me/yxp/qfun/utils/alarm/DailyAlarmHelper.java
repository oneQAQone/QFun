package me.yxp.qfun.utils.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Calendar;

import me.yxp.qfun.utils.qq.HostInfo;

public class DailyAlarmHelper {
    private static final String ALARM_ACTION = "ALARM_ACTION";

    private static void registerReceiver(BroadcastReceiver receiver) {
        HostInfo.getHostContext().registerReceiver(receiver, new IntentFilter(ALARM_ACTION));
    }
    public static void setupDailyAlarm(int hour, int minute, int second, int requestCode, BroadcastReceiver receiver) {
        Context context = HostInfo.getHostContext();
        registerReceiver(receiver);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARM_ACTION);
        intent.putExtra("result_code", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = calculateNextTriggerTime(hour, minute, second);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    private static long calculateNextTriggerTime(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);


        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis();
    }

    public static void cancelDailyAlarm(int requestCode) {
        Context context = HostInfo.getHostContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isAlarmSet(int requestCode) {
        Context context = HostInfo.getHostContext();
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent != null;
    }
}