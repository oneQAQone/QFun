package me.yxp.qfun.hook.msg;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import me.yxp.qfun.hook.api.OnSendMsg;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;

@HookItemAnnotation(TAG = "修改图片外显", desc = "点击可设置图片外显文字")
public final class PicSummaryHook extends BaseWithDataHookItem {
    private String mPicSummary;
    private OnSendMsg.SendMsgListener mChangeSummaryListener;

    @Override
    protected void initCallback() {
        mChangeSummaryListener = msgElements -> {
            if (mPicSummary == null) {
                return;
            }

            for (Object msgElement : msgElements) {
                Object picElement = FieldUtils.create(msgElement).withName("picElement").getValue();
                Object marketFaceElement = FieldUtils.create(msgElement).withName("marketFaceElement").getValue();

                if (picElement != null) {
                    FieldUtils.create(picElement).withName("summary").setValue(mPicSummary);
                }
                if (marketFaceElement != null) {
                    FieldUtils.create(marketFaceElement).withName("faceName").setValue(mPicSummary);
                }
            }
        };
    }

    @Override
    public void startHook() {
        OnSendMsg.INSTANCE.addListener(mChangeSummaryListener);
    }

    @Override
    public void stopHook() {
        OnSendMsg.INSTANCE.removeListener(mChangeSummaryListener);
    }

    @Override
    public void initData() {
        mPicSummary = (String) DataUtils.deserialize("data", "picsummary");
    }

    @Override
    public void savaData() {
        DataUtils.serialize("data", "picsummary", mPicSummary);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        EditText editText = new EditText(context);
        editText.setHint("留空则默认显示");

        new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("设置图片外显文本")
                .setView(editText)
                .setPositiveButton("保存", (dialog, which) -> {
                    String input = editText.getText().toString();
                    if (input.isEmpty()) {
                        mPicSummary = null;
                    } else {
                        mPicSummary = input;
                    }
                })
                .create()
                .show();
    }
}