package me.yxp.qfun.hook.api;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yxp.qfun.R;
import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnMsgMenuOpen extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnMsgMenuOpen();
    private static final String PREFIX = "[QFun]";

    private final Map<String, OnMsgMenuClickListener> mActionMap = new HashMap<>();

    @Override
    public void loadHook() throws Throwable {
        Class<?> itemClass = DexKit.getClass(getClass().getSimpleName());
        Class<?> itemSuperclass = itemClass.getSuperclass();

        Method setMenu = MethodUtils.create(ClassUtils._QQCustomMenuExpandableLayout())
                .withMethodName("setMenu")
                .findOne();
        Method setView = MethodUtils.create(ClassUtils._QQCustomMenuExpandableLayout())
                .withReturnType(View.class)
                .withParamTypes(int.class, itemClass, boolean.class, float[].class)
                .findOne();

        HookUtils.hookAlways(setMenu, param -> {
            List<Object> items = (List<Object>) FieldUtils.create(param.args[0])
                    .ofType(List.class)
                    .inParent(param.args[0].getClass().getSuperclass())
                    .getValue();
            Object aIOMsgItem = FieldUtils.create(items.get(0))
                    .ofType(ClassUtils._AIOMsgItem())
                    .inParent(itemSuperclass)
                    .getValue();
            Object msgRecord = FieldUtils.create(aIOMsgItem)
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();
            String msgType = FieldUtils.create(msgRecord)
                    .withName("msgType")
                    .getValue()
                    .toString();

            for (String name : mActionMap.keySet()) {
                if (containsItem(items, name)) {
                    continue;
                }

                String[] args = name.split(",");
                List<String> result = Arrays.asList(Arrays.copyOfRange(args, 3, args.length));

                if (!result.isEmpty() && !result.contains(msgType)) {
                    continue;
                }

                addMenuItem(items, itemClass, itemSuperclass, name, aIOMsgItem);
            }
        }, null);

        HookUtils.replaceAlways(setView, param -> {
            Object menuItem = param.args[1];
            Object aIOMsgItem = FieldUtils.create(menuItem)
                    .ofType(ClassUtils._AIOMsgItem())
                    .inParent(itemSuperclass)
                    .getValue();
            Object msgRecord = FieldUtils.create(aIOMsgItem)
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();
            Object obj = FieldUtils.create(menuItem)
                    .ofType(String.class)
                    .getValue();

            if (obj == null || !obj.toString().startsWith(PREFIX)) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            return createMenuItemView(obj.toString(), msgRecord);
        });
    }

    private void addMenuItem(List<Object> items, Class<?> itemClass, Class<?> itemSuperclass,
                             String name, Object aIOMsgItem) {
        Object item = ClassUtils.makeDefaultObject(itemClass, QQCurrentEnv.getActivity(), aIOMsgItem);
        FieldUtils.create(item)
                .ofType(boolean.class)
                .inParent(itemSuperclass)
                .setValue(false);
        FieldUtils.create(item)
                .ofType(int.class)
                .inParent(itemSuperclass)
                .setValue(0);
        FieldUtils.create(item)
                .ofType(String.class)
                .setValue(name);
        items.add(0, item);
    }

    private View createMenuItemView(String menuKey, Object msgRecord) {
        Activity activity = QQCurrentEnv.getActivity();
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(activity)
                .inflate(R.layout.msgmenuitem, null);
        TextView msgmenuitemTextView = itemLayout.findViewById(R.id.msgmenuitemTextView);

        String name = menuKey.split(",")[2];
        msgmenuitemTextView.setText(name);

        itemLayout.setOnClickListener(view -> {
            try {
                mActionMap.get(menuKey).onClick(new MsgData(msgRecord));
            } catch (Throwable th) {
                ErrorOutput.Error(getClass().getSimpleName(), th);
            }
        });

        return itemLayout;
    }

    private boolean containsItem(List<Object> list, String name) throws Throwable {
        Class<?> itemClass = DexKit.getClass(getClass().getSimpleName());

        for (Object item : list) {
            if (item.getClass() != itemClass) {
                continue;
            }

            String itemValue = FieldUtils.create(item)
                    .ofType(String.class)
                    .getValue()
                    .toString();

            if (name.equals(itemValue)) {
                return true;
            }
        }
        return false;
    }

    public void addListener(String key, OnMsgMenuClickListener listener) {
        mActionMap.put(key, listener);
    }

    public void removeListener(String key) {
        mActionMap.remove(key);
    }

    public interface OnMsgMenuClickListener extends Listener {
        void onClick(MsgData msgData) throws Throwable;
    }
}