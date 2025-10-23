package me.yxp.qfun.javaplugin.api;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import bsh.BshMethod;
import bsh.Interpreter;
import me.yxp.qfun.hook.api.OnMsg;
import me.yxp.qfun.hook.api.OnPaiYiPai;
import me.yxp.qfun.hook.api.OnSendMsg;
import me.yxp.qfun.hook.api.OnTroopJoin;
import me.yxp.qfun.hook.api.OnTroopQuit;
import me.yxp.qfun.hook.api.OnTroopShutUp;
import me.yxp.qfun.javaplugin.loader.PluginCompiler;
import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.utils.error.PluginError;
import me.yxp.qfun.utils.reflect.FieldUtils;

public class PluginCallback {
    private final PluginInfo mPluginInfo;
    private final Interpreter mInterpreter;
    public OnMsg.MsgListener onMsg;
    public OnTroopJoin.TroopJoinListener joinGroup;
    public OnTroopQuit.TroopQuitListener quitGroup;
    public OnTroopShutUp.TroopShutUpListener shutUpGroup;
    public OnSendMsg.SendMsgListener getMsg;
    public OnPaiYiPai.PaiYiPaiListener onPai;
    public Map<String, String> menuItemMap = new LinkedHashMap<>();

    public PluginCallback(PluginCompiler pluginCompiler) {
        mPluginInfo = pluginCompiler.pluginInfo;
        mInterpreter = pluginCompiler.interpreter;

        initCallbacks();
    }

    private void initCallbacks() {
        onMsg = msgRecord -> new Thread(() -> {
            for (BshMethod bshMethod : mInterpreter.getNameSpace().getMethods()) {
                if (bshMethod.getName().equals("onMsg") &&
                        Arrays.equals(bshMethod.getParameterTypes(), new Class[]{Object.class})) {
                    try {
                        MsgData msgData = new MsgData(msgRecord);
                        bshMethod.invoke(new Object[]{msgData}, mInterpreter);
                    } catch (Exception e) {
                        PluginError.callError(e, mPluginInfo);
                    }
                }
            }
        }).start();

        joinGroup = (troopUin, memberUin) ->
                runOnNewThread("joinGroup", new Class[]{String.class, String.class},
                        new Object[]{troopUin, memberUin});

        quitGroup = (troopUin, memberUin) ->
                runOnNewThread("quitGroup", new Class[]{String.class, String.class},
                        new Object[]{troopUin, memberUin});

        shutUpGroup = (troopUin, memberUin, time, opUin) ->
                runOnNewThread("shutUpGroup", new Class[]{String.class, String.class, long.class, String.class},
                        new Object[]{troopUin, memberUin, time, opUin});

        getMsg = msgElements -> {
            for (int i = 0; i < msgElements.size(); i++) {
                Object textElement = FieldUtils.create(msgElements.get(i)).withName("textElement").getValue();
                if (textElement == null) continue;

                String content = FieldUtils.create(textElement).withName("content").getValue().toString();
                String myContent = "";

                try {
                    if (Arrays.asList(mInterpreter.getNameSpace().getMethodNames()).contains("getMsg")) {
                        myContent = mInterpreter.getNameSpace()
                                .getMethod("getMsg", new Class[]{String.class})
                                .invoke(new Object[]{content}, mInterpreter).toString();
                    }
                } catch (Exception e) {
                    PluginError.callError(e, mPluginInfo);
                }

                FieldUtils.create(textElement).withName("content")
                        .setValue(myContent.isEmpty() ? content : myContent);
            }
        };

        onPai = (peerUin, chatType, fromUin) ->
                runOnNewThread("onPaiYiPai", new Class[]{String.class, int.class, String.class},
                        new Object[]{peerUin, chatType, fromUin});
    }

    public void unLoadPlugin() {
        invokeMethodIfExists("unLoadPlugin", new Class[0], new Object[0]);
    }

    public void chatInterface(int chatType, String peerUin, String peerName) {
        invokeMethodIfExists("chatInterface",
                new Class[]{int.class, String.class, String.class},
                new Object[]{chatType, peerUin, peerName});
    }

    public void invokeItem(String callback, Object... params) {
        boolean findError = true;

        // 尝试查找参数为 (int, String, String) 的方法
        try {
            BshMethod bshMethod = findMethod(callback,
                    new Class[]{int.class, String.class, String.class});
            if (bshMethod != null) {
                findError = false;
                bshMethod.invoke(new Object[]{params[0], params[1], params[2]}, mInterpreter);
            }
        } catch (Exception e) {
            PluginError.callError(e, mPluginInfo);
        }

        // 尝试查找参数为 (int, String, String, Object) 的方法
        try {
            BshMethod bshMethod = findMethod(callback,
                    new Class[]{int.class, String.class, String.class, Object.class});
            if (bshMethod != null) {
                findError = false;
                bshMethod.invoke(params, mInterpreter);
            }

            if (findError) {
                bshMethod.invoke(params, mInterpreter);
            }
        } catch (Exception e) {
            if (!findError) {
                PluginError.callError(e, mPluginInfo);
            } else {
                PluginError.findError(e, mPluginInfo, callback);
            }
        }
    }

    public void invokeMenuItem(String callback, MsgData msgData) {
        boolean findError = true;
        try {
            BshMethod bshMethod = findMethod(callback, new Class[]{Object.class});
            findError = bshMethod == null;
            bshMethod.invoke(new Object[]{msgData}, mInterpreter);
        } catch (Exception e) {
            if (!findError) {
                PluginError.callError(e, mPluginInfo);
            } else {
                PluginError.findError(e, mPluginInfo, callback);
            }
        }
    }

    private BshMethod findMethod(String name, Class<?>[] paramTypes) {
        for (BshMethod method : mInterpreter.getNameSpace().getMethods()) {
            if (method.getName().equals(name) &&
                    Arrays.equals(method.getParameterTypes(), paramTypes)) {
                return method;
            }
        }
        return null;
    }

    private void invokeMethodIfExists(String methodName, Class<?>[] paramTypes, Object[] args) {
        if (Arrays.asList(mInterpreter.getNameSpace().getMethodNames()).contains(methodName)) {
            try {
                mInterpreter.getNameSpace().getMethod(methodName, paramTypes)
                        .invoke(args, mInterpreter);
            } catch (Exception e) {
                PluginError.callError(e, mPluginInfo);
            }
        }
    }

    private void runOnNewThread(String methodName, Class<?>[] paramTypes, Object[] args) {
        new Thread(() -> invokeMethodIfExists(methodName, paramTypes, args)).start();
    }
}