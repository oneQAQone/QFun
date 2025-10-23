package me.yxp.qfun.hook.api;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnGetMsgRecord extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnGetMsgRecord();

    @Override
    public void loadHook() throws Throwable {
        Method getMsgRecord = MethodUtils.create(ClassUtils._AIOMsgItem())
                .withMethodName("getMsgRecord")
                .findOne();

        HookUtils.hookAlways(getMsgRecord, null, param -> {
            Object msgRecord = param.getResult();
            for (Listener listener : mListenerSet) {
                if (listener instanceof GetMsgRecordListener) {
                    ((GetMsgRecordListener) listener).onGet(msgRecord);
                }
            }
        });
    }

    public interface GetMsgRecordListener extends Listener {
        void onGet(Object msgRecord) throws Throwable;
    }
}