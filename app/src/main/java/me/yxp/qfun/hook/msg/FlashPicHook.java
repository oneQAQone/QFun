package me.yxp.qfun.hook.msg;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.yxp.qfun.hook.api.OnAIOViewUpdate;
import me.yxp.qfun.hook.api.OnGetMsgRecord;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.reflect.FieldUtils;

@HookItemAnnotation(TAG = "闪照破解", desc = "以图片方式直接显示闪照")
public final class FlashPicHook extends BaseSwitchHookItem {
    private OnAIOViewUpdate.AIOViewUpdateListener mAddViewListener;
    private OnGetMsgRecord.GetMsgRecordListener mGetMsgListener;

    @Override
    protected void initCallback() {
        mGetMsgListener = msgRecord -> {
            int subMsgType = (int) FieldUtils.create(msgRecord).withName("subMsgType").getValue();
            if (subMsgType != 8194) {
                return;
            }

            FieldUtils.create(msgRecord).withName("subMsgType").setValue(subMsgType & ~8192);
            FieldUtils.create(msgRecord).withName("guildName").setValue("我是闪照");
        };

        mAddViewListener = (frameLayout, msgRecord) -> {
            MsgData msgData = new MsgData(msgRecord);
            if (msgData.msgType != 2) {
                return;
            }

            if ("我是闪照".equals(FieldUtils.create(msgRecord).withName("guildName").getValue().toString())) {
                addFlashView(frameLayout);
            } else {
                removeFlashView(frameLayout);
            }
        };
    }

    @Override
    public void startHook() {
        OnGetMsgRecord.INSTANCE.addListener(mGetMsgListener);
        OnAIOViewUpdate.INSTANCE.addListener(mAddViewListener);
    }

    @Override
    public void stopHook() {
        OnGetMsgRecord.INSTANCE.removeListener(mGetMsgListener);
        OnAIOViewUpdate.INSTANCE.removeListener(mAddViewListener);
    }

    private void addFlashView(FrameLayout frameLayout) {
        TextView flashView = new TextView(frameLayout.getContext());
        flashView.setTextSize(30);
        flashView.setTextColor(Color.BLUE);
        flashView.setText("我是闪照");

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        frameLayout.addView(flashView, params);
    }

    private void removeFlashView(FrameLayout frameLayout) {
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            View view = frameLayout.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if ("我是闪照".equals(textView.getText().toString())) {
                    textView.setVisibility(View.GONE);
                }
            }
        }
    }
}