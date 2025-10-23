package me.yxp.qfun.javaplugin.loader;

import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import me.yxp.qfun.hook.api.OnMsg;
import me.yxp.qfun.hook.api.OnMsgMenuOpen;
import me.yxp.qfun.hook.api.OnPaiYiPai;
import me.yxp.qfun.hook.api.OnSendMsg;
import me.yxp.qfun.hook.api.OnTroopJoin;
import me.yxp.qfun.hook.api.OnTroopQuit;
import me.yxp.qfun.hook.api.OnTroopShutUp;
import me.yxp.qfun.javaplugin.api.PluginCallback;
import me.yxp.qfun.javaplugin.api.PluginMethod;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PluginCompiler {
    public PluginInfo pluginInfo;
    public PluginCallback pluginCallback;
    public FixClassLoader fixClassLoader;
    public Interpreter interpreter;
    public Map<String, String> itemMap = new LinkedHashMap<>();
    private String mScript;

    public PluginCompiler(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
        this.interpreter = new Interpreter();
        this.pluginCallback = new PluginCallback(this);
    }

    private void setValues() {
        try {
            fixClassLoader = new FixClassLoader();
            interpreter.set("context", HostInfo.getHostContext());
            interpreter.set("myUin", QQCurrentEnv.getCurrentUin());
            interpreter.set("classLoader", ClassUtils.getHostClassLoader());
            interpreter.set("pluginPath", pluginInfo.pluginPath);
            interpreter.set("pluginId", pluginInfo.pluginId);
            interpreter.setClassLoader(fixClassLoader);
        } catch (Exception e) {
            // 忽略设置异常
        }
    }

    private void registerMethod() {
        NameSpace nameSpace = interpreter.getNameSpace();
        PluginMethod pluginMethod = new PluginMethod(this);
        try {
            for (Method method : PluginMethod.class.getDeclaredMethods()) {
                nameSpace.setMethod(new BshMethod(method, pluginMethod));
            }
        } catch (Exception e) {
            // 忽略方法注册异常
        }
    }

    private void loadScript(String pluginPath) {
        File file = new File(pluginPath + "main.java");

        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream dis = new DataInputStream(fis);
             BufferedReader br = new BufferedReader(new InputStreamReader(dis))) {

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            mScript = sb.toString();
        } catch (Exception e) {
            // 忽略脚本加载异常
        }
    }

    public void startPlugin() throws EvalError {
        stopPlugin();
        loadScript(pluginInfo.pluginPath);
        setValues();
        registerMethod();
        interpreter.eval(mScript, pluginInfo.pluginPath + "main.java");
        registerCallback();
        pluginInfo.isRunning = true;
    }

    public void stopPlugin() {
        removeCallback();
        pluginCallback.unLoadPlugin();
        itemMap.clear();
        interpreter.getNameSpace().clear();
        pluginInfo.isRunning = false;
    }

    public void addItem(String name, String callback) {
        itemMap.put(name, callback);
    }

    public void addMenuItem(String name, String callback) {
        pluginCallback.menuItemMap.put(name, callback);
    }

    private void registerCallback() {
        OnSendMsg.INSTANCE.addListener(pluginCallback.getMsg);
        OnMsg.INSTANCE.addListener(pluginCallback.onMsg);
        OnTroopJoin.INSTANCE.addListener(pluginCallback.joinGroup);
        OnTroopQuit.INSTANCE.addListener(pluginCallback.quitGroup);
        OnTroopShutUp.INSTANCE.addListener(pluginCallback.shutUpGroup);
        OnPaiYiPai.INSTANCE.addListener(pluginCallback.onPai);

        for (String key : pluginCallback.menuItemMap.keySet()) {
            ((OnMsgMenuOpen) OnMsgMenuOpen.INSTANCE).addListener(key,
                    msgData -> pluginCallback.invokeMenuItem(pluginCallback.menuItemMap.get(key), msgData));
        }
    }

    private void removeCallback() {
        OnSendMsg.INSTANCE.removeListener(pluginCallback.getMsg);
        OnMsg.INSTANCE.removeListener(pluginCallback.onMsg);
        OnTroopJoin.INSTANCE.removeListener(pluginCallback.joinGroup);
        OnTroopQuit.INSTANCE.removeListener(pluginCallback.quitGroup);
        OnTroopShutUp.INSTANCE.removeListener(pluginCallback.shutUpGroup);
        OnPaiYiPai.INSTANCE.removeListener(pluginCallback.onPai);

        for (String key : pluginCallback.menuItemMap.keySet()) {
            ((OnMsgMenuOpen) OnMsgMenuOpen.INSTANCE).removeListener(key);
        }
    }

    public static class FixClassLoader extends ClassLoader {
        private final List<ClassLoader> mLoaders;

        private FixClassLoader() {
            super(ClassLoader.getSystemClassLoader());
            mLoaders = new ArrayList<>();
            mLoaders.add(getSystemClassLoader());
            mLoaders.add(ClassUtils.getModuleClassLoader());
            mLoaders.add(ClassUtils.getHostClassLoader());
            mLoaders.add(XposedBridge.class.getClassLoader());
        }

        @Override
        public Class<?> findClass(String className) {
            for (ClassLoader classLoader : mLoaders) {
                try {
                    return classLoader.loadClass(className);
                } catch (Exception unused) {
                    // 尝试下一个ClassLoader
                }
            }
            return null;
        }

        @Override
        public URL getResource(String resourceName) {
            for (ClassLoader classLoader : mLoaders) {
                URL resource = classLoader.getResource(resourceName);
                if (resource != null) {
                    return resource;
                }
            }
            return super.getResource(resourceName);
        }

        @Override
        public Enumeration<URL> getResources(String resourceName) {
            ArrayList<URL> urlList = new ArrayList<>();
            for (ClassLoader classLoader : mLoaders) {
                try {
                    Enumeration<URL> resources = classLoader.getResources(resourceName);
                    while (resources.hasMoreElements()) {
                        urlList.add(resources.nextElement());
                    }
                } catch (Exception e) {
                    // 忽略资源获取异常
                }
            }
            return Collections.enumeration(urlList);
        }

        @Override
        public Class<?> loadClass(String className, boolean resolve) {
            for (ClassLoader classLoader : mLoaders) {
                try {
                    return classLoader.loadClass(className);
                } catch (Exception unused) {
                    // 尝试下一个ClassLoader
                }
            }
            return null;
        }

        public void addClassLoader(ClassLoader classLoader) {
            mLoaders.add(classLoader);
        }
    }
}