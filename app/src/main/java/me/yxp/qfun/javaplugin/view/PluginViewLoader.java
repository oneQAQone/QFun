package me.yxp.qfun.javaplugin.view;

import java.lang.reflect.Method;

import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.javaplugin.loader.PluginManager;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class PluginViewLoader {
    private static PluginView sPluginView;

    public static void loadFloatButton() throws Throwable {
        sPluginView = new PluginView();

        Class<?> ChatFragment = ClassUtils.load("com.tencent.aio.main.fragment.ChatFragment");

        Method onHiddenChanged = MethodUtils.create(ChatFragment)
                .withMethodName("onHiddenChanged")
                .findOne();

        HookUtils.hookAlways(onHiddenChanged, null, param -> {

            boolean status = (boolean) param.args[0];
            SyncUtils.post(() -> {
                for (PluginInfo pluginInfo : PluginManager.pluginInfos) {
                    if (pluginInfo.isRunning && QQCurrentEnv.getActivity() != null) {
                        if (status) {
                            sPluginView.dismissFloatButton();
                            return;
                        } else {
                            try {
                                sPluginView.initPopupWindow();
                                sPluginView.showFloatingButton();
                                return;
                            } catch (Throwable th) {
                                ErrorOutput.Error("PluginViewLoader", th);
                            }
                        }
                    }
                }
            });
        });

    }

}