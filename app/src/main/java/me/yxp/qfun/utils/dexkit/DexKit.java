package me.yxp.qfun.utils.dexkit;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.view.View;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.query.matchers.MethodsMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.ClassDataList;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.wrap.DexClass;
import org.luckypray.dexkit.wrap.DexMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.yxp.qfun.hook.MainHook;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class DexKit {
    private static Map<String, String> sClassMap = new HashMap<>();
    private static Map<String, String> sMethodMap = new HashMap<>();

    public static void checkVersion() throws Throwable {
        readData();
        if (VersionCheck.checkVersion()) {
            showFindDialog();
        } else {
            if (sClassMap.isEmpty() || sMethodMap.isEmpty()) {
                showFindDialog();
            } else {
                MainHook.hook();
            }
        }
    }

    private static void readData() {
        Object classData = DataUtils.deserialize("dexkit", "classMap");
        Object methodData = DataUtils.deserialize("dexkit", "methodMap");

        if (classData != null) sClassMap = (Map<String, String>) classData;
        if (methodData != null) sMethodMap = (Map<String, String>) methodData;
    }

    private static void saveData() {
        DataUtils.serialize("dexkit", "lastVersionMap", VersionCheck.getLatestVersionMap());
        DataUtils.serialize("dexkit", "classMap", sClassMap);
        DataUtils.serialize("dexkit", "methodMap", sMethodMap);
    }

    private static void showFindDialog() throws Throwable {
        XposedHelpers.findAndHookMethod(ClassUtils.load("com.tencent.mobileqq.activity.SplashActivity"),
                "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        Context context = (Context) param.thisObject;
                        AlertDialog dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                .setTitle("查找方法")
                                .setMessage("查找方法中，请耐心等待")
                                .setCancelable(false)
                                .create();
                        dialog.show();

                        SyncUtils.runOnNewThread("DexKit", () -> {
                            try {
                                doFind();
                                saveData();
                                dialog.setMessage("查找完成，保存方法并关闭应用");
                                Thread.sleep(500);
                                Process.killProcess(Process.myPid());
                            } catch (Throwable e) {
                                // 忽略查找过程中的异常
                            } finally {
                                dialog.dismiss();
                            }
                        });
                    }
                });
    }

    private static void doFind() throws Throwable {
        sClassMap.clear();
        sMethodMap.clear();

        BridgeControler.initBridge(HostInfo.getHostContext().getApplicationInfo().sourceDir);
        DexKitBridge bridge = BridgeControler.getBridge();

        try {
            findClasses(bridge);
            findMethods(bridge);
        } finally {
            BridgeControler.closeBridge();
        }
    }

    private static void findClasses(DexKitBridge bridge) throws Throwable {
        // OnMsg
        ClassData onMsgClass = bridge.findClass(FindClass.create()
                        .searchPackages("com.tencent.qqnt.msg")
                        .matcher(ClassMatcher.create()
                                .methods(MethodsMatcher.create()
                                        .methods(List.of(
                                                MethodMatcher.create().name("onAddSendMsg"),
                                                MethodMatcher.create().name("onRecvMsg")
                                        ))
                                )
                        )
                ).stream()
                .filter(classData -> classData.getName().contains("MsgService"))
                .collect(Collectors.toList())
                .get(0);
        if (onMsgClass != null) {
            sClassMap.put("OnMsg", onMsgClass.toDexType().serialize());
        }

        // RemoveReplyAtHook
        ClassData removeReplyAtHook = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.aio.input.reply")
                .matcher(ClassMatcher.create()
                        .methods(MethodsMatcher.create()
                                .methods(List.of(
                                        MethodMatcher.create().name("accept"),
                                        MethodMatcher.create().returnType(void.class)
                                                .paramTypes(ClassUtils._AIOMsgItem())
                                ))
                        )
                )
        ).singleOrNull();
        if (removeReplyAtHook != null) {
            sClassMap.put("RemoveReplyAtHook", removeReplyAtHook.toDexType().serialize());
        }

        // RedPacketHook
        ClassData redPacketHook = bridge.findClass(FindClass.create()
                .matcher(ClassMatcher.create()
                        .usingStrings("QWalletHttp-QWalletPbServlet", "qwallet_pb_handle_trpc_error")
                        .methods(MethodsMatcher.create()
                                .methods(List.of(
                                        MethodMatcher.create().name("onReceive"),
                                        MethodMatcher.create().name("onSend")
                                ))
                        )
                )
        ).singleOrNull();
        if (redPacketHook != null) {
            sClassMap.put("RedPacketHook", redPacketHook.toDexType().serialize());
        }

        // GrabVoiceRedPacket
        ClassData grabVoiceRedPacket = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.qqnt.qwallet.bigdata")
                .matcher(ClassMatcher.create()
                        .methods(MethodsMatcher.create()
                                .methods(List.of(MethodMatcher.create().name("onError")))
                        )
                )
        ).singleOrNull();
        if (grabVoiceRedPacket != null) {
            sClassMap.put("GrabVoiceRedPacket", grabVoiceRedPacket.toDexType().serialize());
        }

        // SimpleTroopManagement
        ClassData simpleTroopManagement = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.aio.msglist.holder.component.avatar")
                .matcher(ClassMatcher.create().addInterface(View.OnClickListener.class.getName()))
        ).singleOrNull();
        if (simpleTroopManagement != null) {
            sClassMap.put("SimpleTroopManagement", simpleTroopManagement.toDexType().serialize());
        }

        // CreateElement
        ClassData createElement = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.qqnt.msg")
                .matcher(ClassMatcher.create()
                        .usingStrings("ArkMsgModel", "toAppXml fail, metaList, err="))
        ).singleOrNull();
        if (createElement != null) {
            sClassMap.put("CreateElement", createElement.toDexType().serialize());
        }

        // QQSettingInject
        ClassData qqSettingInject = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.setting.processor")
                .matcher(ClassMatcher.create().usingStrings("context", "leftText"))
        ).singleOrNull();
        if (qqSettingInject != null) {
            sClassMap.put("QQSettingInject", qqSettingInject.toDexType().serialize());
        }

        // CookieTool
        ClassData cookieTool = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.pskey")
                .matcher(ClassMatcher.create().usingStrings("pskey", "puskey"))
        ).singleOrNull();
        if (cookieTool != null) {
            sClassMap.put("CookieTool", cookieTool.toDexType().serialize());
        }

        // OnMsgMenuOpen
        ClassData onMsgMenuOpen = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.qqnt.aio.menu")
                .matcher(ClassMatcher.create().usingStrings("CopyMenuItem"))
        ).singleOrNull();
        if (onMsgMenuOpen != null) {
            sClassMap.put("OnMsgMenuOpen", onMsgMenuOpen.toDexType().serialize());
        }

        // QQHomeInjectClass1
        ClassData qqHomeInjectClass1 = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.activity.recent")
                .matcher(ClassMatcher.create().addInterface("com.tencent.widget.PopupMenuDialog$OnClickActionListener"))
        ).singleOrNull();
        if (qqHomeInjectClass1 != null) {
            sClassMap.put("QQHomeInjectClass1", qqHomeInjectClass1.toDexType().serialize());
        }

        // RecordsReplyMsgHook
        ClassData recordsReplyMsgHook = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.aio.msglist.msgrepo")
                .matcher(ClassMatcher.create().usingStrings("AIOMsgRepo MsgReplyAbility"))
        ).singleOrNull();
        if (recordsReplyMsgHook != null) {
            sClassMap.put("RecordsReplyMsgHook", recordsReplyMsgHook.toDexType().serialize());
        }

        // ForwardPttHook
        ClassData forwardPttHook = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.qqnt.aio.menu")
                .matcher(ClassMatcher.create().usingStrings("ForwardMenuItem"))
        ).singleOrNull();
        if (forwardPttHook != null) {
            sClassMap.put("ForwardPttHook", forwardPttHook.toDexType().serialize());
        }

        // QQHomeInjectClass2
        ClassDataList qqHomeInjectClass2List = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.activity.recent")
                .matcher(ClassMatcher.create()
                        .usingStrings("Conversation")
                        .addInterface(View.OnClickListener.class.getName()))
        );
        if (qqHomeInjectClass2List.singleOrNull() != null) {
            ClassData qqHomeInjectClass2 = qqHomeInjectClass2List.singleOrNull();
            sClassMap.put("QQHomeInjectClass2", qqHomeInjectClass2.toDexType().serialize());

            Method conversationPlusBuild = MethodUtils.create(ClassUtils.load("com.tencent.widget.PopupMenuDialog"))
                    .withMethodName("conversationPlusBuild")
                    .findOne();
            MethodData methodData = bridge.findMethod(FindMethod.create()
                    .searchInClass(qqHomeInjectClass2List)
                    .matcher(MethodMatcher.create().addInvoke(MethodMatcher.create(conversationPlusBuild)))
            ).singleOrNull();
            if (methodData != null) {
                sMethodMap.put("QQHomeInjectMethod", methodData.toDexMethod().serialize());
            }
        }

        //RemoveFilteredVideoHook
        ClassData removeFilteredVideoHook = bridge.findClass(FindClass.create()
                .searchPackages("com.tencent.mobileqq.aio.shortcurtbar")
                .matcher(ClassMatcher.create()
                        .usingStrings("originList")
                        .usingStrings("filterVideoItem"))
        ).singleOrNull();
        if (removeFilteredVideoHook != null) {
            sClassMap.put("RemoveFilteredVideoHook", removeFilteredVideoHook.toDexType().serialize());
        }
    }

    private static void findMethods(DexKitBridge bridge) throws Throwable {
        // WebviewHook
        MethodData webviewHook = bridge.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                        .declaredClass(ClassUtils._WebSecurityPluginV2())
                        .returnType(boolean.class)
                        .paramTypes(String.class)
                        .usingStrings("notDisableSecurityCheck url="))
        ).singleOrNull();
        if (webviewHook != null) {
            sMethodMap.put("WebviewHook", webviewHook.toDexMethod().serialize());
        }

        // AntiAtAllHook
        MethodData antiAtAllHook = bridge.findMethod(FindMethod.create()
                .searchPackages("com.tencent.mobileqq.notification.modularize")
                .matcher(MethodMatcher.create()
                        .usingStrings("TianShuOfflineMsgCenter", "deal0x135Msg online:"))
        ).singleOrNull();
        if (antiAtAllHook != null) {
            sMethodMap.put("AntiAtAllHook", antiAtAllHook.toDexMethod().serialize());
        }

        // ForwardPttHook
        MethodData forwardPttHook = bridge.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                        .declaredClass(ClassUtils._NtMsgForwardUtils())
                        .returnType(boolean.class)
                        .usingStrings("startForwardMsgV2 origin_msg_type="))
        ).singleOrNull();
        if (forwardPttHook != null) {
            sMethodMap.put("ForwardPttHook", forwardPttHook.toDexMethod().serialize());
        }

        //OnTroopJoin
        MethodData onTroopJoin = bridge.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                        .declaredClass(ClassUtils._TroopOnlinePushHandler())
                        .returnType(void.class)
                        .paramTypes(String.class, String.class, String.class)
                        .usingStrings("handleMemberAdd addMemberUin:"))
        ).singleOrNull();
        if (onTroopJoin != null) {
            sMethodMap.put("OnTroopJoin", onTroopJoin.toDexMethod().serialize());
        }

    }

    public static Class<?> getClass(String key) throws Throwable {
        DexClass dexClass = new DexClass(sClassMap.get(key));
        return dexClass.getInstance(ClassUtils.getHostClassLoader());
    }

    public static Method getMethod(String key) throws Throwable {
        DexMethod dexMethod = new DexMethod(sMethodMap.get(key));
        return dexMethod.getMethodInstance(ClassUtils.getHostClassLoader());
    }
}