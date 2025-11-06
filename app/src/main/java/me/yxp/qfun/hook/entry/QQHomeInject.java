package me.yxp.qfun.hook.entry;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import me.yxp.qfun.R;
import me.yxp.qfun.activity.InjectSettings;
import me.yxp.qfun.activity.JavaPlugin;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class QQHomeInject {

    public static void hook() throws Throwable {

        Class<?> OnClickActionListener = ClassUtils.load("com.tencent.widget.PopupMenuDialog$OnClickActionListener");

        Method conversationPlusBuild = MethodUtils.create(ClassUtils._PopupMenuDialog())
                .withMethodName("conversationPlusBuild")
                .findOne();

        HookUtils.hookAlways(conversationPlusBuild, param -> {

            List<Object> menuItemList = (List<Object>) param.args[1];
            Object origin = param.args[2];

            param.args[2] = Proxy.newProxyInstance(ClassUtils.getHostClassLoader(), new Class[]{OnClickActionListener}, (proxy, method, args) ->
                    handleOnClickAction(origin, method, args));

            Class<?> menuItemClass = menuItemList.get(0).getClass();

            menuItemList.add(0, ClassUtils.makeDefaultObject(menuItemClass, R.string.plugin_name, "JavaPlugin", "JavaPlugin", R.drawable.plugin));
            menuItemList.add(0, ClassUtils.makeDefaultObject(menuItemClass, R.string.app_name, "QFun", "JavaPlugin", R.drawable.ic_launcher));
        }, null);

    }

    private static Object handleOnClickAction(Object origin, Method method, Object[] args) throws Throwable {

        Object menuItem = args[0];
        int id = (int) FieldUtils.create(menuItem).withName("id").getValue();

        Activity activity = QQCurrentEnv.getActivity();

        if (activity != null) {

            Class<?> activityClass = null;
            if (id == R.string.plugin_name) {
                activityClass = JavaPlugin.class;
            } else if (id == R.string.app_name) {
                activityClass = InjectSettings.class;
            }

            if (activityClass != null) {
                activity.startActivity(new Intent(activity, activityClass));
                return null;
            }

        }

       return method.invoke(origin, args);


    }

}