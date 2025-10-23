package me.yxp.qfun.hook.api;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.List;

import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnAIOViewUpdate extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnAIOViewUpdate();
    private static final String GRAY_TIPS_MSG_ITEM = "com.tencent.mobileqq.aio.msg.GrayTipsMsgItem";
    private static final String BUBBLE_LAYOUT_CLASS = "com.tencent.qqnt.aio.holder.template.BubbleLayoutCompatPress";
    private static final String HOOK_TAG = "HOOK";

    @Override
    public void loadHook() throws Throwable {
        Method onMsgViewUpdate = MethodUtils.create(ClassUtils._AIOBubbleMsgItemVB())
                .withReturnType(void.class)
                .withParamTypes(int.class, ClassUtils._AIOMsgItem(), List.class, Bundle.class)
                .findOne();

        HookUtils.hookAlways(onMsgViewUpdate, param -> {
            ViewGroup msgView = (ViewGroup) FieldUtils.create(param.thisObject)
                    .ofType(View.class)
                    .getValue();
            Object aIOMsgItem = param.args[1];

            if (GRAY_TIPS_MSG_ITEM.equals(aIOMsgItem.getClass().getName())) {
                return;
            }

            Object msgRecord = FieldUtils.create(aIOMsgItem)
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();

            LinearLayout msgLayout = findMsgLayout(msgView);
            if (msgLayout == null) {
                return;
            }

            FrameLayout frameLayout = setupFrameLayout(msgLayout);
            for (Listener listener : mListenerSet) {
                if (listener instanceof AIOViewUpdateListener) {
                    ((AIOViewUpdateListener) listener).onUpdate(frameLayout, msgRecord);
                }
            }
        }, null);
    }

    private LinearLayout findMsgLayout(ViewGroup msgView) {
        for (int i = 0; i < msgView.getChildCount(); i++) {
            View child = msgView.getChildAt(i);
            if (BUBBLE_LAYOUT_CLASS.equals(child.getClass().getName())) {
                return (LinearLayout) child;
            }
        }
        return null;
    }

    private FrameLayout setupFrameLayout(LinearLayout msgLayout) {
        FrameLayout frameLayout;

        if (msgLayout.getChildAt(0).getTag() == null
                || !HOOK_TAG.equals(msgLayout.getChildAt(0).getTag().toString())) {
            Context context = msgLayout.getContext();
            frameLayout = new FrameLayout(context);
            frameLayout.setTag(HOOK_TAG);

            View originalView = msgLayout.getChildAt(0);
            LinearLayout linearLayout = new LinearLayout(context);

            msgLayout.removeAllViews();
            linearLayout.addView(originalView);
            frameLayout.addView(linearLayout);
            msgLayout.addView(frameLayout);
        } else {
            frameLayout = (FrameLayout) msgLayout.getChildAt(0);
        }

        return frameLayout;
    }

    public interface AIOViewUpdateListener extends Listener {
        void onUpdate(FrameLayout frameLayout, Object msgRecord) throws Throwable;
    }
}