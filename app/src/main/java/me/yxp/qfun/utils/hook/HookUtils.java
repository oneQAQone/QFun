package me.yxp.qfun.utils.hook;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodReplacement;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;

public class HookUtils {

    public static void hookAllConstructIfEnable(BaseSwitchHookItem hookItem, Class<?> clazz, Hooker hookBefore, Hooker hookAfter) {

        try {
            XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookBefore == null || !hookItem.getHookStatus())
                        return;
                    try {
                        hookBefore.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.itemHookError(hookItem, th);
                    }
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookAfter == null || !hookItem.getHookStatus())
                        return;
                    try {
                        hookAfter.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.itemHookError(hookItem, th);
                    }
                }
            });
        } catch (Exception e) {
            ErrorOutput.itemHookError(hookItem, e);
        }

    }

    public static void hookAlways(Method method, Hooker hookBefore, Hooker hookAfter) {
        try {
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookBefore == null)
                        return;

                    try {
                        hookBefore.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.Error("", th);
                    }
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookAfter == null)
                        return;

                    try {
                        hookAfter.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.Error("", th);
                    }
                }
            });
        } catch (Exception e) {
            ErrorOutput.Error("", e);
        }
    }

    public static void replaceAlways(Method method, Replacement replaceMent) {
        try {
            XposedBridge.hookMethod(method, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        return replaceMent.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.Error("", th);
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                }
            });
        } catch (Exception e) {
            ErrorOutput.Error("", e);
        }
    }

    public static void hookIfEnable(BaseSwitchHookItem hookItem, Method method, Hooker hookBefore, Hooker hookAfter) {

        try {
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookBefore == null || !hookItem.getHookStatus())
                        return;

                    try {
                        hookBefore.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.itemHookError(hookItem, th);
                    }
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (hookAfter == null || !hookItem.getHookStatus())
                        return;

                    try {
                        hookAfter.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.itemHookError(hookItem, th);
                    }
                }
            });
        } catch (Exception e) {

            ErrorOutput.itemHookError(hookItem, e);

        }
    }

    public static void replaceIfEnable(BaseSwitchHookItem hookItem, Method method, Replacement replaceMent) {
        try {
            XposedBridge.hookMethod(method, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        if (!hookItem.getHookStatus())
                            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        return replaceMent.execute(param);
                    } catch (Throwable th) {
                        ErrorOutput.itemHookError(hookItem, th);
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                }
            });
        } catch (Exception e) {

            ErrorOutput.itemHookError(hookItem, e);

        }
    }

    public interface Hooker {
        void execute(XC_MethodHook.MethodHookParam param) throws Throwable;
    }

    public interface Replacement {
        Object execute(XC_MethodHook.MethodHookParam param) throws Throwable;
    }
}

