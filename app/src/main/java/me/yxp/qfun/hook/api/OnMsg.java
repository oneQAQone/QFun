package me.yxp.qfun.hook.api;

import java.lang.reflect.Method;
import java.util.ArrayList;

import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnMsg extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnMsg();

    @Override
    public void loadHook() throws Throwable {
        Class<?> msgService = DexKit.getClass(getClass().getSimpleName());

        Method onOtherMsg = MethodUtils.create(msgService)
                .withMethodName("onRecvMsg")
                .findOne();
        Method onSelfMsg = MethodUtils.create(msgService)
                .withMethodName("onAddSendMsg")
                .findOne();

        HookUtils.hookAlways(onOtherMsg, null, param -> {
            ArrayList<Object> msgList = (ArrayList<Object>) param.args[0];
            Object msgRecord = msgList.get(0);
            execute(msgRecord);
        });

        HookUtils.hookAlways(onSelfMsg, null, param -> {
            Object msgRecord = param.args[0];
            execute(msgRecord);
        });
    }

    private void execute(Object msgRecord) throws Throwable {
        ArrayList<Object> elements = (ArrayList<Object>) FieldUtils.create(msgRecord)
                .withName("elements")
                .getValue();

        if (elements.isEmpty()) {
            return;
        }

        for (Listener listener : mListenerSet) {
            if (listener instanceof MsgListener) {
                ((MsgListener) listener).onMsg(msgRecord);
            }
        }
    }

    public interface MsgListener extends Listener {
        void onMsg(Object msgRecord) throws Throwable;
    }
}

