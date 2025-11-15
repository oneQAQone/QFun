package me.yxp.qfun.hook.troop;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;
import java.time.LocalTime;

import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.EnableInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.LoopHolder;
import me.yxp.qfun.utils.thread.SyncUtils;
import me.yxp.qfun.utils.ui.EnableDialog;

@HookItemAnnotation(TAG = "群打卡", desc = "点击选择你要打卡的群聊")
public final class TroopClockInHook extends BaseWithDataHookItem {
    private static LoopHolder sLoopHolder;
    private static Method sTroopClockIn;
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

        sLoopHolder = new LoopHolder();
        sLoopHolder.setRunnable(() -> {

            LocalTime now = LocalTime.now();
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalTime startRange = midnight.plusSeconds(2);
            LocalTime endRange = midnight.plusSeconds(1);
            boolean isAroundMidnight = (now.isAfter(startRange) || now.equals(startRange))
                    && (now.isBefore(endRange) || now.equals(endRange));
            if (isAroundMidnight) {
                sLoopHolder.setSleepTime(50);
                doClockIn();
            } else {
                sLoopHolder.setSleepTime(1000);
            }

        });
    }

    @Override
    public void startHook() {
        doClockIn();
        sLoopHolder.start();
    }

    @Override
    public void stopHook() {
        sLoopHolder.stop();
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

    private void doClockIn() {
        Object troopClockInHandler;
        String currentUin = QQCurrentEnv.getCurrentUin();
        try {
            troopClockInHandler = ClassUtils.makeDefaultObject(
                    ClassUtils._TroopClockInHandler(), QQCurrentEnv.getQQAppInterface());
        } catch (Throwable th) {
            ErrorOutput.itemHookError(this, th);
            return;
        }
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
    }
}