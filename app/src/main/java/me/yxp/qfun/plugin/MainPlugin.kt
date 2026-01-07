package me.yxp.qfun.plugin

import me.yxp.qfun.plugin.loader.PluginManager

object MainPlugin {
    fun initAllPluginForCurrent() {
        PluginManager.stopAllPlugins()
        PluginManager.plugins.clear()
        PluginManager.loadAll()
        PluginManager.autoStart()
    }

}