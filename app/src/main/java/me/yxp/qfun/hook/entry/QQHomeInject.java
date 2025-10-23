package me.yxp.qfun.hook.entry;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.Method;
import java.util.List;

import me.yxp.qfun.R;
import me.yxp.qfun.activity.InjectSettings;
import me.yxp.qfun.activity.JavaPlugin;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class QQHomeInject {

    public static void hook() throws Throwable {
        Class<?> menuItem = ClassUtils.load("com.tencent.widget.PopupMenuDialog$MenuItem");

        Method initMenuItem = DexKit.getMethod("QQHomeInjectMethod");
        Method onClickAction = MethodUtils.create(DexKit.getClass("QQHomeInjectClass1"))
                .withMethodName("onClickAction")
                .findOne();
        Method makeItem = MethodUtils.create(DexKit.getClass("QQHomeInjectClass2"))
                .withReturnType(menuItem)
                .withParamTypes(int[].class)
                .findOne();

        HookUtils.hookAlways(initMenuItem, param -> {
            List<Object> menuItemList = (List<Object>) FieldUtils.create(param.thisObject)
                    .ofType(List.class)
                    .getValue();
            menuItemList.add(0, makeItem.invoke(param.thisObject,
                    new int[]{R.string.plugin_name, R.drawable.plugin, R.color.BLACK}));
            menuItemList.add(0, makeItem.invoke(param.thisObject,
                    new int[]{R.string.app_name, R.drawable.ic_launcher, R.color.BLACK}));
        }, null);

        HookUtils.hookAlways(onClickAction, param -> {
            Object menuItemObj = param.args[0];
            int id = (int) FieldUtils.create(menuItemObj).withName("id").getValue();

            Activity activity = QQCurrentEnv.getActivity();
            Intent intent;

            if (id == R.string.plugin_name) {
                intent = new Intent(activity, JavaPlugin.class);
            } else if (id == R.string.app_name) {
                intent = new Intent(activity, InjectSettings.class);
            } else {
                return;
            }

            activity.startActivity(intent);
        }, null);
    }
}