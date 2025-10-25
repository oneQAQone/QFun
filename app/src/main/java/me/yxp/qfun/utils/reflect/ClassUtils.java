package me.yxp.qfun.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.utils.qq.HostInfo;

public class ClassUtils {
    private static ClassLoader sHostClassLoader;

    public static ClassLoader getHostClassLoader() {
        return sHostClassLoader;
    }

    public static void setHostClassLoader(ClassLoader classLoader) {
        sHostClassLoader = classLoader;
    }

    public static ClassLoader getModuleClassLoader() {
        return ClassUtils.class.getClassLoader();
    }

    public static Class<?> load(String className) throws Exception {
        try {
            return sHostClassLoader.loadClass(className);
        } catch (Exception e) {
            return Class.forName(className);
        }
    }

    public static Class<?> loadFromPlugin(String pluginName, String className) throws Exception {
        Class<?> pluginStatic = load("com.tencent.mobileqq.pluginsdk.PluginStatic");
        Method getOrCreateClassLoader = MethodUtils.create(pluginStatic)
                .withMethodName("getOrCreateClassLoader")
                .findOne();
        ClassLoader pluginClassLoader = (ClassLoader) getOrCreateClassLoader.invoke(null,
                HostInfo.getHostContext(), pluginName);
        return pluginClassLoader.loadClass(className);
    }

    public static BaseSwitchHookItem getHookItem(Class<?> clazz) {
        try {
            return (BaseSwitchHookItem) makeDefaultObject(clazz);
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static Object makeDefaultObject(Class<?> clazz, Object... args) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            try {
                return constructor.newInstance(args);
            } catch (Exception ignored) {
                // 尝试下一个构造函数
            }
        }
        throw new RuntimeException("创建" + clazz.getName() + "对象失败");
    }

    // 以下为预定义的类加载方法
    public static Class<?> _NewSettingConfigProvider() throws Exception {
        return load("com.tencent.mobileqq.setting.main.NewSettingConfigProvider");
    }

    public static Class<?> _MainSettingConfigProvider() throws Exception {
        return load("com.tencent.mobileqq.setting.main.MainSettingConfigProvider");
    }

    public static Class<?> _TroopClockInHandler() throws Exception {
        return load("com.tencent.mobileqq.troop.clockin.handler.TroopClockInHandler");
    }

    public static Class<?> _QQAppInterface() throws Exception {
        return load("com.tencent.mobileqq.app.QQAppInterface");
    }

    public static Class<?> _PaiYiPaiHandler() throws Exception {
        return load("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler");
    }

    public static Class<?> _IKernelMsgService$CppProxy() throws Exception {
        return load("com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService$CppProxy");
    }

    public static Class<?> _MsgRecord() throws Exception {
        return load("com.tencent.qqnt.kernel.nativeinterface.MsgRecord");
    }

    public static Class<?> _AIOMsgItem() throws Exception {
        return load("com.tencent.mobileqq.aio.msg.AIOMsgItem");
    }

    public static Class<?> _Contact() throws Exception {
        return load("com.tencent.qqnt.kernelpublic.nativeinterface.Contact");
    }

    public static Class<?> _QQNTWrapperSession() throws Exception {
        return load("com.tencent.qqnt.kernel.nativeinterface.IQQNTWrapperSession$CppProxy");
    }

    public static Class<?> _AIOBubbleMsgItemVB() throws Exception {
        return load("com.tencent.mobileqq.aio.msglist.holder.AIOBubbleMsgItemVB");
    }

    public static Class<?> _NotificationFacade() throws Exception {
        return load("com.tencent.qqnt.notification.NotificationFacade");
    }

    public static Class<?> _WebSecurityPluginV2() throws Exception {
        return load("com.tencent.mobileqq.webview.WebSecurityPluginV2");
    }

    public static Class<?> _AIOAvatarContentComponent() throws Exception {
        return load("com.tencent.mobileqq.aio.msglist.holder.component.avatar.AIOAvatarContentComponent");
    }

    public static Class<?> _QQCustomMenuExpandableLayout() throws Exception {
        return load("com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout");
    }

    public static Class<?> _MSFServlet() throws Exception {
        return load("mqq.app.MSFServlet");
    }

    public static Class<?> _FromServiceMsg() throws Exception {
        return load("com.tencent.qphone.base.remote.FromServiceMsg");
    }

    public static Class<?> _ToServiceMsg() throws Exception {
        return load("com.tencent.qphone.base.remote.ToServiceMsg");
    }

    public static Class<?> _AIOMsgFollowComponent() throws Exception {
        return load("com.tencent.mobileqq.aio.msglist.holder.component.msgfollow.AIOMsgFollowComponent");
    }

    public static Class<?> _CardHandler() throws Exception {
        return load("com.tencent.mobileqq.app.CardHandler");
    }

    public static Class<?> _PadUtil() throws Exception {
        return load("com.tencent.common.config.pad.PadUtil");
    }

    public static Class<?> _AppRuntime() throws Exception {
        return load("mqq.app.AppRuntime");
    }

    public static Class<?> _NtMsgForwardUtils() throws Exception {
        return load("com.tencent.qqnt.aio.forward.NtMsgForwardUtils");
    }

    public static Class<?> _PhotoPanelVB() throws Exception {
        return load("com.tencent.mobileqq.aio.panel.photo.PhotoPanelVB");
    }

    public static Class<?> _TroopOnlinePushHandler() throws Exception {
        return ClassUtils.load("com.tencent.mobileqq.troop.onlinepush.api.impl.TroopOnlinePushHandler");
    }

    public static Class<?> _InteractivePopManager() throws  Exception {
        return load("com.tencent.mobileqq.springhb.interactive.ui.InteractivePopManager");
    }

    public static Class<?> _DeviceInfoMonitor() throws Exception {
        return load("com.tencent.qmethod.pandoraex.monitor.DeviceInfoMonitor");
    }
}
