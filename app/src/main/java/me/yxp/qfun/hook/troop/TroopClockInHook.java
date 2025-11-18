package me.yxp.qfun.hook.troop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.alarm.DailyAlarmHelper;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.EnableInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.LoopHolder;
import me.yxp.qfun.utils.thread.SyncUtils;
import me.yxp.qfun.utils.ui.EnableDialog;

@HookItemAnnotation(TAG = "群打卡", desc = "点击选择你要打卡的群聊", TargetProcess = "All")
public final class TroopClockInHook extends BaseWithDataHookItem {
    public static final int CLOCK_PREPARE = 1001;
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
                if (intent.getIntExtra("result_code", 0) != CLOCK_PREPARE) return;
                startLoop();
                setAlarm();
            }
        };

        mLoopHolder = new LoopHolder();
        mLoopHolder.setRunnable(this::doClockIn);
    }

    @Override
    public void startHook() {
        doClockInImmediately();
        if (DailyAlarmHelper.isAroundMidnight(180000, 0)) {
            startLoop();
        }
        setAlarm();
    }

    @Override
    public void stopHook() {
        mLoopHolder.stop();
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
            DailyAlarmHelper.setupDailyAlarm(23, 57, 0, CLOCK_PREPARE, receiver);
        }
    }

    public void cancelAlarm() {
        if (DailyAlarmHelper.isAlarmSet(CLOCK_PREPARE)) {
            DailyAlarmHelper.cancelDailyAlarm(CLOCK_PREPARE);
        }
    }

    private void startLoop() {
        long stopTime = ((System.currentTimeMillis() + 28800000) / 86400000 + 1) * 86400000 + 60000;
        mLoopHolder.setStopTime(stopTime);
        mLoopHolder.start();
    }

    private void doClockInImmediately() {
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

    private void doClockIn() {

        if (!DailyAlarmHelper.isAroundMidnight(0, 60000)) return;
        doClockInImmediately();
        mLoopHolder.stop();

    }

}