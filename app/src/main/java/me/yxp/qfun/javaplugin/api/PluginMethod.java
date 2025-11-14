package me.yxp.qfun.javaplugin.api;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import me.yxp.qfun.javaplugin.loader.LoadJarHelper;
import me.yxp.qfun.javaplugin.loader.PluginCompiler;
import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.utils.error.PluginError;
import me.yxp.qfun.utils.json.JsonConfigUtils;
import me.yxp.qfun.utils.qq.CookieTool;
import me.yxp.qfun.utils.qq.FriendTool;
import me.yxp.qfun.utils.qq.MsgTool;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.ToastUtils;
import me.yxp.qfun.utils.qq.TroopTool;
import me.yxp.qfun.utils.thread.SyncUtils;

public class PluginMethod {
    private final PluginCompiler mPluginCompiler;
    private final PluginInfo mPluginInfo;
    private final String mConfigPath;

    public PluginMethod(PluginCompiler pluginCompiler) {
        mPluginCompiler = pluginCompiler;
        mPluginInfo = pluginCompiler.pluginInfo;
        mConfigPath = mPluginInfo.pluginPath + "config/";
    }

    // 插件管理方法
    public void loadJar(String jarPath) {
        try {
            mPluginCompiler.fixClassLoader.addClassLoader(LoadJarHelper.loadJar(jarPath));
        } catch (Exception e) {
            PluginError.callError(e, mPluginInfo);
        }
    }

    public void loadJava(String path) {
        try {
            mPluginCompiler.interpreter.source(path);
        } catch (Exception e) {
            PluginError.callError(e, mPluginInfo);
        }
    }

    // UI相关方法
    public void Toast(Object message) {
        ToastUtils.Toast(message.toString());
    }

    public void QQToast(int icon, Object message) {
        ToastUtils.QQToast(icon, message.toString());
    }

    public Activity getNowActivity() {
        return QQCurrentEnv.getActivity();
    }

    // 菜单项管理
    public void addItem(String name, String callback) {
        mPluginCompiler.addItem(name, callback);
    }

    public void addMenuItem(String name, String callback) {
        mPluginCompiler.addMenuItem("[QFun]," + mPluginInfo.pluginId + "," + name, callback);
    }

    public void addMenuItem(String name, String callback, int[] msgTypes) {
        String types = Arrays.stream(msgTypes).mapToObj(String::valueOf).collect(Collectors.joining(","));
        mPluginCompiler.addMenuItem("[QFun]," + mPluginInfo.pluginId + "," + name + "," + types, callback);
    }

    // 好友相关方法
    public List<HashMap<String, Object>> getAllFriend() {
        return executeWithErrorHandling(FriendTool::getAllFriend, null);
    }

    public boolean isFriend(String uin) {
        return executeWithErrorHandling(() -> FriendTool.isFriend(uin), false);
    }

    public String getUidFromUin(String uin) {
        return executeWithErrorHandling(() -> FriendTool.getUidFromUin(uin), "");
    }

    public String getUinFromUid(String uid) {
        return executeWithErrorHandling(() -> FriendTool.getUidFromUin(uid), "");
    }

    public void sendZan(String uin, int num) {
        executeWithErrorHandling(() -> FriendTool.sendZan(uin, num));
    }

    // 群组管理方法
    public void shutUp(String troopUin, String uin, long time) {
        executeWithErrorHandling(() -> TroopTool.shutUp(troopUin, uin, time));
    }

    public void shutUpAll(String troopUin, boolean bool) {
        executeWithErrorHandling(() -> TroopTool.shutUpAll(troopUin, bool));
    }

    public boolean isShutUp(String troopUin) {
        return executeWithErrorHandling(() -> TroopTool.isShutUp(troopUin), false);
    }

    public void setGroupAdmin(String troopUin, String uin, boolean bool) {
        executeWithErrorHandling(() -> TroopTool.setGroupAdmin(troopUin, uin, bool));
    }

    public void kickGroup(String troopUin, String uin, boolean bool) {
        executeWithErrorHandling(() -> TroopTool.kickGroup(troopUin, uin, bool));
    }

    public void setGroupMemberTitle(String troopUin, String uin, String title) {
        executeWithErrorHandling(() -> TroopTool.setGroupMemberTitle(troopUin, uin, title));
    }

    public List<HashMap<String, Object>> getGroupList() {
        return executeWithErrorHandling(TroopTool::getGroupList, null);
    }

    public List<HashMap<String, Object>> getGroupMemberList(String troopUin) {
        return executeWithErrorHandling(() -> TroopTool.getGroupMemberList(troopUin), null);
    }

    public List<HashMap<String, Object>> getProhibitList(String troopUin) {
        return executeWithErrorHandling(() -> TroopTool.getProhibitList(troopUin), null);
    }

    public Object getTroopInfo(String troopUin) {
        return executeWithErrorHandling(() -> TroopTool.getTroopInfo(troopUin), null);
    }

    public Object getMemberInfo(String troopUin, String uin) {
        return executeWithErrorHandling(() -> TroopTool.getMemberInfo(troopUin, uin), null);
    }

    public String getMemberName(String troopUin, String uin) {
        return executeWithErrorHandling(() -> TroopTool.getMemberName(troopUin, uin), "");
    }

    public void changeMemberName(String troopUin, String uin, String name) {
        executeWithErrorHandling(() -> TroopTool.changeMemberName(troopUin, uin, name));
    }

    // 消息管理方法
    public void recallMsg(int type, String peer, ArrayList<Long> list) {
        executeWithErrorHandling(() -> MsgTool.recallMsg(type, peer, list));
    }

    public void recallMsg(Object contact, ArrayList<Long> list) {
        executeWithErrorHandling(() -> MsgTool.recallMsg(contact, list));
    }

    public void sendMsg(String peerUin, String msg, int type) {
        executeWithErrorHandling(() -> MsgTool.sendMsg(peerUin, msg, type));
    }

    public void sendMsg(Object contact, String msg) {
        executeWithErrorHandling(() -> MsgTool.sendMsg(contact, msg));
    }

    public void sendPic(String peerUin, String path, int type) {
        executeWithErrorHandling(() -> MsgTool.sendPic(peerUin, path, type));
    }

    public void sendPic(Object contact, String path) {
        executeWithErrorHandling(() -> MsgTool.sendPic(contact, path));
    }

    public void sendPtt(String peerUin, String path, int type) {
        executeWithErrorHandling(() -> MsgTool.sendPtt(peerUin, path, type));
    }

    public void sendPtt(Object contact, String path) {
        executeWithErrorHandling(() -> MsgTool.sendPtt(contact, path));
    }

    public void sendCard(String peerUin, String data, int type) {
        executeWithErrorHandling(() -> MsgTool.sendCard(peerUin, data, type));
    }

    public void sendCard(Object contact, String data) {
        executeWithErrorHandling(() -> MsgTool.sendCard(contact, data));
    }

    public void sendVideo(String peerUin, String path, int type) {
        executeWithErrorHandling(() -> MsgTool.sendVideo(peerUin, path, type));
    }

    public void sendVideo(Object contact, String path) {
        executeWithErrorHandling(() -> MsgTool.sendVideo(contact, path));
    }

    public void sendFile(String peerUin, String path, int type) {
        executeWithErrorHandling(() -> MsgTool.sendFile(peerUin, path, type));
    }

    public void sendFile(Object contact, String path) {
        executeWithErrorHandling(() -> MsgTool.sendFile(contact, path));
    }

    public void sendReplyMsg(String peerUin, long replyMsgId, String msg, int type) {
        executeWithErrorHandling(() -> MsgTool.sendReplyMsg(peerUin, replyMsgId, msg, type));
    }

    public void sendReplyMsg(Object contact, long replyMsgId, String msg) {
        executeWithErrorHandling(() -> MsgTool.sendReplyMsg(contact, replyMsgId, msg));
    }

    // Cookie相关方法
    public String getRealSkey() {
        return executeWithErrorHandling(CookieTool::getRealSkey, null);
    }

    public String getSkey() {
        return executeWithErrorHandling(CookieTool::getSkey, null);
    }

    public String getPskey(String url) {
        return executeWithErrorHandling(() -> CookieTool.getPskey(url), null);
    }

    public String getGTK(String url) {
        return executeWithErrorHandling(() -> CookieTool.getGTK(url), null);
    }

    public String getStweb() {
        return executeWithErrorHandling(CookieTool::getStweb, null);
    }

    public String getPt4Token(String url) {
        return executeWithErrorHandling(() -> CookieTool.getPt4Token(url), null);
    }

    public long getBkn(String key) {
        return executeWithErrorHandling(() -> CookieTool.getBkn(key), 0L);
    }

    public String getFriendRKey() {
        return executeWithErrorHandling(CookieTool::getFriendRKey, "");
    }

    public String getGroupRKey() {
        return executeWithErrorHandling(CookieTool::getGroupRKey, "");
    }

    // 配置管理方法
    public void putString(String configName, String key, String value) {
        executeWithErrorHandling(() -> JsonConfigUtils.putString(mConfigPath, configName, key, value));
    }

    public void putInt(String configName, String key, int value) {
        executeWithErrorHandling(() -> JsonConfigUtils.putInt(mConfigPath, configName, key, value));
    }

    public void putBoolean(String configName, String key, boolean value) {
        executeWithErrorHandling(() -> JsonConfigUtils.putBoolean(mConfigPath, configName, key, value));
    }

    public void putLong(String configName, String key, long value) {
        executeWithErrorHandling(() -> JsonConfigUtils.putLong(mConfigPath, configName, key, value));
    }

    public String getString(String configName, String key, String defaultValue) {
        return executeWithErrorHandling(() ->
                JsonConfigUtils.getString(mConfigPath, configName, key, defaultValue), defaultValue);
    }

    public int getInt(String configName, String key, int defaultValue) {
        return executeWithErrorHandling(() ->
                JsonConfigUtils.getInt(mConfigPath, configName, key, defaultValue), defaultValue);
    }

    public boolean getBoolean(String configName, String key, boolean defaultValue) {
        return executeWithErrorHandling(() ->
                JsonConfigUtils.getBoolean(mConfigPath, configName, key, defaultValue), defaultValue);
    }

    public long getLong(String configName, String key, long defaultValue) {
        return executeWithErrorHandling(() ->
                JsonConfigUtils.getLong(mConfigPath, configName, key, defaultValue), defaultValue);
    }

    // 统一的错误处理方法
    private void executeWithErrorHandling(SyncUtils.MyRunnable action) {
        try {
            action.run();
        } catch (Throwable throwable) {
            PluginError.callError(throwable, mPluginInfo);
        }
    }

    private <T> T executeWithErrorHandling(SupplierWithException<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            PluginError.callError(e, mPluginInfo);
            return defaultValue;
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}