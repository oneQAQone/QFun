package me.yxp.qfun.hook.chat;

import java.util.HashMap;

import me.yxp.qfun.hook.api.OnGetMsgRecord;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.FieldUtils;

@HookItemAnnotation(TAG = "默认气泡和字体", desc = "在聊天界面中将他人的气泡和字体全部显示为默认")
public final class DefaultBubbleAndFontHook extends BaseSwitchHookItem {
    private OnGetMsgRecord.GetMsgRecordListener mOnGetListener;

    @Override
    protected void initCallback() {
        mOnGetListener = msgRecord -> {
            String senderUin = FieldUtils.create(msgRecord)
                    .withName("senderUin")
                    .getValue()
                    .toString();
            if (!senderUin.equals(QQCurrentEnv.getCurrentUin())) {
                FieldUtils.create(msgRecord)
                        .withName("msgAttrs")
                        .setValue(new HashMap<>());
            }
        };
    }

    @Override
    public void startHook() {
        OnGetMsgRecord.INSTANCE.addListener(mOnGetListener);
    }

    @Override
    public void stopHook() {
        OnGetMsgRecord.INSTANCE.removeListener(mOnGetListener);
    }
}