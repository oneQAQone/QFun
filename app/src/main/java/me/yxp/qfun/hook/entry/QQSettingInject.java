package me.yxp.qfun.hook.entry;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function0;
import me.yxp.qfun.R;
import me.yxp.qfun.activity.InjectSettings;
import me.yxp.qfun.activity.JavaPlugin;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class QQSettingInject {
    private static final String TOP_TITLE = "模块";
    private static final String BOTTOM_TITLE = "";
    private static final int MODULE_ORDER = 10;

    public static void hook() throws Throwable {

        HookUtils.hookAlways(getItemList(), null, param -> {
            final Context context = (Context) param.args[0];
            List<Object> result = (List<Object>) param.getResult();

            Class<?> itemClass = DexKit.getClass("QQSettingInjectClass1");

            Object settingEntry = createSettingEntry(context, itemClass);
            Object pluginEntry = createPluginEntry(context, itemClass);

            setupClickListener(context, itemClass, settingEntry, pluginEntry);

            Class<?> itemGroup = result.get(0).getClass();
            ArrayList<Object> moduleList = new ArrayList<>(2);
            moduleList.add(settingEntry);
            moduleList.add(pluginEntry);

            Object group = ClassUtils.makeDefaultObject(itemGroup, moduleList, TOP_TITLE, BOTTOM_TITLE, 0, null);

            result.add(1, group);
        });
    }

    private static Method getItemList() throws Throwable {

        Class<?> providerClass;

        if (HostInfo.isQQ() && HostInfo.getVersionCode() >= 12288) {
            providerClass = DexKit.getClass("QQSettingInjectClass2");
        } else {

            try {
                providerClass = ClassUtils._NewSettingConfigProvider();
            } catch (Throwable throwable) {
                providerClass = ClassUtils._MainSettingConfigProvider();
            }
        }

        return MethodUtils.create(providerClass)
                .withReturnType(List.class)
                .withParamTypes(Context.class)
                .findOne();
    }

    private static Object createSettingEntry(Context context, Class<?> itemClass) {
        try {
            return ClassUtils.makeDefaultObject(itemClass, context, MODULE_ORDER,
                    "QFun", R.drawable.ic_launcher, null);
        } catch (Throwable th) {
            return ClassUtils.makeDefaultObject(itemClass, context, MODULE_ORDER,
                    "QFun", R.drawable.ic_launcher);
        }
    }

    private static Object createPluginEntry(Context context, Class<?> itemClass) {
        try {
            return ClassUtils.makeDefaultObject(itemClass, context, MODULE_ORDER,
                    "JavaPlugin", R.drawable.plugin, null);
        } catch (Throwable th) {
            return ClassUtils.makeDefaultObject(itemClass, context, MODULE_ORDER,
                    "JavaPlugin", R.drawable.plugin);
        }
    }

    private static void setupClickListener(Context context, Class<?> itemClass,
                                           Object settingEntry, Object pluginEntry) throws Exception {
        Method setOnClickListener = MethodUtils.create(itemClass)
                .withReturnType(void.class)
                .withParamTypes(ClassUtils.load(Function0.class.getName()))
                .findOne();

        Class<?> function0Class = setOnClickListener.getParameterTypes()[0];
        ClassLoader hostClassLoader = ClassUtils.getHostClassLoader();
        final Object unitInstance = hostClassLoader
                .loadClass("kotlin.Unit")
                .getField("INSTANCE")
                .get(null);

        Object settingsFunc = createProxyFunction(hostClassLoader, function0Class, unitInstance,
                () -> context.startActivity(new Intent(context, InjectSettings.class)));

        Object pluginFunc = createProxyFunction(hostClassLoader, function0Class, unitInstance,
                () -> context.startActivity(new Intent(context, JavaPlugin.class)));

        setOnClickListener.invoke(settingEntry, settingsFunc);
        setOnClickListener.invoke(pluginEntry, pluginFunc);
    }

    private static Object createProxyFunction(ClassLoader classLoader, Class<?> functionInterface,
                                              Object unitInstance, Runnable action) {
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{functionInterface},
                (proxy, method, args) -> {
                    if ("invoke".equals(method.getName())) {
                        action.run();
                    }
                    return unitInstance;
                });
    }
}