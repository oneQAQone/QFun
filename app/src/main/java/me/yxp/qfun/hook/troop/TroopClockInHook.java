package me.yxp.qfun.hook.troop;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.alarm.DailyAlarmHelper;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.EnableInfo;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.LoopHolder;
import me.yxp.qfun.utils.thread.SyncUtils;
import me.yxp.qfun.utils.ui.EnableDialog;

@HookItemAnnotation(TAG = "群打卡", desc = "点击选择你要打卡的群聊", TargetProcess = "All")
public final class TroopClockInHook extends BaseWithDataHookItem {
    public static final int CLOCK_PREPARE = 1001;
    public static final int CLOCK_COMPLETED = 1002;
    private static Method sTroopClockIn;
    public LoopHolder mLoopHolder;
    private BroadcastReceiver receiver;
    private EnableInfo mTroopEnableInfo;

    @Override
    protected boolean initMethod() throws Throwable {
        sTroopClockIn = MethodUtils.create(ClassUtils._TroopClockInHandler())
                .withReturnType(void.class)
                .withParamTypes(String.class, String.class)
                .findOne();
        return true;
    }

    @Override
    protected void initCallback() {
        mTroopEnableInfo = new EnableInfo.TroopEnableInfo("TroopClockIn");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultCode = intent.getIntExtra("result_code", 0);
                if (resultCode == CLOCK_PREPARE) {
                    mLoopHolder.start();
                    DailyAlarmHelper.setupDailyAlarm(23, 55, 0, CLOCK_PREPARE, receiver);
                } else if (resultCode == CLOCK_COMPLETED) {
                    mLoopHolder.stop();
                    DailyAlarmHelper.cancelDailyAlarm(CLOCK_COMPLETED);
                }
            }
        };

        mLoopHolder = new LoopHolder();
        mLoopHolder.setSleepTime(0);
        mLoopHolder.setRunnable(() -> {

            if (isAroundMidnight(100, 200)) {
                doClockIn();
                Thread.sleep(40);
            }

            if (!isInForeground()) {
                if (isAroundMidnight(2500,  1000)) {
                    doClockIn();
                    Thread.sleep(100);
                }
            }

        });
    }

    @Override
    public void startHook() {
        doClockIn();
        setAlarm();
    }

    @Override
    public void stopHook() {
        cancelAlarm();
    }

    @Override
    public void initData() {
        mTroopEnableInfo.initInfo();
    }

    @Override
    public void savaData() {
        mTroopEnableInfo.savaInfo();
    }

    @Override
    public void onClick(View v) {

        Context context = v.getContext();
        mTroopEnableInfo.updateInfo();
        new EnableDialog(context, mTroopEnableInfo).show();

    }

    public void setAlarm() {
        if (!DailyAlarmHelper.isAlarmSet(CLOCK_PREPARE)) {
            DailyAlarmHelper.setupDailyAlarm(23, 55, 0, CLOCK_PREPARE, receiver);
        }
        if (!DailyAlarmHelper.isAlarmSet(CLOCK_COMPLETED)) {
            DailyAlarmHelper.setupDailyAlarm(0, 5, 0, CLOCK_COMPLETED, receiver);
        }

    }

    public void cancelAlarm() {
        if (DailyAlarmHelper.isAlarmSet(CLOCK_PREPARE)) {
            DailyAlarmHelper.cancelDailyAlarm(CLOCK_PREPARE);
        }
        if (DailyAlarmHelper.isAlarmSet(CLOCK_COMPLETED)) {
            DailyAlarmHelper.cancelDailyAlarm(CLOCK_COMPLETED);
        }

    }

    private boolean isAroundMidnight(long before, long after) {
        long now = System.currentTimeMillis() + 28800000;
        long remain = now % 86400000;
        return remain <= after || remain >= 86400000 - before;
    }

    private void doClockIn() {

        try {
            String currentUin = QQCurrentEnv.getCurrentUin();

            Object troopClockInHandler = ClassUtils.makeDefaultObject(
                    ClassUtils._TroopClockInHandler(), QQCurrentEnv.getQQAppInterface());


            for (String troopUin : mTroopEnableInfo.dataList.getKeyArray()) {
                if (mTroopEnableInfo.dataList.getIsAvailable(troopUin)) {
                    SyncUtils.async(() -> {
                        try {
                            sTroopClockIn.invoke(troopClockInHandler, troopUin, currentUin);
                        } catch (Exception e) {
                            ErrorOutput.itemHookError(this, e);
                        }
                    });
                }
            }
        } catch (Throwable th) {
            ErrorOutput.itemHookError(this, th);
        }

    }

    private boolean isInForeground() {
        ActivityManager activityManager = (ActivityManager) HostInfo.getHostContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && Objects.equals(process.processName, HostInfo.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}