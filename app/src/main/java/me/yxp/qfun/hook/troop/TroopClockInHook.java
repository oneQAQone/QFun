package me.yxp.qfun.hook.troop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.lang.reflect.Method;
import java.time.LocalTime;

import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.TroopEnableInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.LoopHolder;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "群打卡", desc = "点击选择你要打卡的群聊")
public final class TroopClockInHook extends BaseWithDataHookItem {
    private static LoopHolder sLoopHolder;
    private static Method sTroopClockIn;
    private TroopEnableInfo mTroopEnableInfo;

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
        mTroopEnableInfo = new TroopEnableInfo("TroopClockIn");

        sLoopHolder = new LoopHolder();
        sLoopHolder.setRunnable(() -> {

            LocalTime now = LocalTime.now();
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalTime startRange = midnight.plusSeconds(2);
            LocalTime endRange = midnight.plusSeconds(2);
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
        
        new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("选择群聊")
                .setMultiChoiceItems(mTroopEnableInfo.getValueArray(), mTroopEnableInfo.getBoolArray(),
                        (DialogInterface dialogInterface, int i, boolean b) -> {
                            String key = mTroopEnableInfo.dataList.getKeyArray()[i];
                            mTroopEnableInfo.dataList.setIsAvailable(key, b);
                        })
                .create()
                .show();

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