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

@HookItemAnnotation(TAG = "群打卡", desc = "点击选择你要打卡的群聊")
public final class TroopClockInHook extends BaseWithDataHookItem {
    private static boolean sHookStatus = false;
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
        mTroopEnableInfo = new TroopEnableInfo("TroopClockin");

        sLoopHolder = new LoopHolder();
        sLoopHolder.setRunnable(() -> {
            for (String troopUin : mTroopEnableInfo.dataList.getKeyArray()) {
                if (mTroopEnableInfo.dataList.getIsAvailable(troopUin)) {
                    new Thread(() -> {
                        try {
                            Object troopClockInHandler = ClassUtils.makeDefaultObject(
                                    ClassUtils._TroopClockInHandler(), QQCurrentEnv.getQQAppInterface());
                            sTroopClockIn.invoke(troopClockInHandler, troopUin, QQCurrentEnv.getCurrentUin());
                        } catch (Exception e) {
                            ErrorOutput.itemHookError(this, e);
                        }
                    }).start();
                }
            }

            LocalTime now = LocalTime.now();
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalTime startRange = midnight.minusMinutes(1);
            LocalTime endRange = midnight.plusMinutes(1);
            boolean isAroundMidnight = (now.isAfter(startRange) || now.equals(startRange))
                    && (now.isBefore(endRange) || now.equals(endRange));

            int sleepTime = isAroundMidnight ? 0 : 1000;
            sLoopHolder.setSleepTime(sleepTime);
        });
    }

    @Override
    public void startHook() {
        if (sHookStatus) {
            return;
        }
        sHookStatus = true;
        sLoopHolder.start();
    }

    @Override
    public void stopHook() {
        if (!sHookStatus) {
            return;
        }
        sHookStatus = false;
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
}