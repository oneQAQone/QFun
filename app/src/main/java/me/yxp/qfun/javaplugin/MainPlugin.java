package me.yxp.qfun.javaplugin;

import me.yxp.qfun.javaplugin.loader.PluginManager;
import me.yxp.qfun.javaplugin.view.PluginViewLoader;
import me.yxp.qfun.utils.error.ErrorOutput;

public class MainPlugin {

    public static void initPlugins() {

        PluginManager.stopAllPlugin();
        PluginManager.pluginInfos.clear();
        PluginManager.getAllPlugin();
        PluginManager.autoLoadPlugin();

    }

    public static void initView() {
        try {
            PluginViewLoader.loadFloatButton();
        } catch (Throwable th) {
            ErrorOutput.Error("MainPlugin", th);
        }
    }

}

