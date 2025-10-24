package me.yxp.qfun.utils.qq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class MsgTool {
    private static Method sRecallMsg;
    private static Method sSendMsg;
    private static Method sGenerateMsgUniqueId;
    private static Method sGetMsgsByMsgId;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("MsgTool", th);
        }
    }

    private static void initMethod() throws Throwable {
        CreateElement.initMethod();

        sRecallMsg = MethodUtils.create(ClassUtils._IKernelMsgService$CppProxy())
                .withMethodName("recallMsg").findOne();
        sSendMsg = MethodUtils.create(ClassUtils._IKernelMsgService$CppProxy())
                .withMethodName("sendMsg").findOne();
        sGenerateMsgUniqueId = MethodUtils.create(ClassUtils._IKernelMsgService$CppProxy())
                .withMethodName("generateMsgUniqueId").findOne();
        sGetMsgsByMsgId = MethodUtils.create(ClassUtils._IKernelMsgService$CppProxy())
                .withMethodName("getMsgsByMsgId").findOne();
    }

    // 撤回消息相关方法
    public static void recallMsg(int type, String peerUin, ArrayList<Long> msgIds) throws Exception {
        recallMsg(makeContact(peerUin, type), msgIds);
    }

    public static void recallMsg(Object contact, long msgId) throws Exception {
        ArrayList<Long> msgIds = new ArrayList<>();
        msgIds.add(msgId);
        recallMsg(contact, msgIds);
    }

    public static void recallMsg(Object contact, ArrayList<Long> msgIds) throws Exception {
        sRecallMsg.invoke(QQCurrentEnv.getKernelMsgservice(), contact, msgIds, null);
    }

    // 发送消息基础方法
    public static void sendMsg(Object contact, List<Object> elements) throws Exception {
        int chatType = (int) FieldUtils.create(contact).withName("chatType").getValue();
        long msgId = generateMsgUniqueId(chatType);
        sSendMsg.invoke(QQCurrentEnv.getKernelMsgservice(), msgId, contact, elements, new HashMap<>(), null);
    }

    public static void sendMsg(String peerUin, String msg, int type) throws Exception {
        sendMsg(makeContact(peerUin, type), msg);
    }

    public static void repeatByMsgRecord(MsgData msgData) throws Exception {

        List<Object> elements = (List<Object>) FieldUtils.create(msgData.data).withName("elements").getValue();
        Map<Integer, Object> msgAttrs = (Map<Integer, Object>) FieldUtils.create(msgData.data).withName("msgAttrs").getValue();
        long msgId = generateMsgUniqueId(msgData.type);
        sSendMsg.invoke(QQCurrentEnv.getKernelMsgservice(), msgId, msgData.contact, elements, msgAttrs, null);

    }

    public static void sendMsg(Object contact, String msg) throws Exception {
        List<Object> msgElements = processMessageContent(contact, msg);
        if (!msgElements.isEmpty()) sendMsg(contact, msgElements);
    }

    // 特殊消息发送方法

    public static void sendTextWithInterface(MsgData msgData, String text, Object proxy) throws Throwable {

        Class<?> IOperateCallback = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback");

        List<Object> msgElements = new ArrayList<>();

        msgElements.add(CreateElement.createTextElement(text));

        long msgId = generateMsgUniqueId(msgData.type);

        Object obj = Proxy.newProxyInstance(ClassUtils.getHostClassLoader(), new Class[]{IOperateCallback},
                (mproxy, method, args) -> {
                    getMsgsByMsgId(msgData.contact, msgId, proxy);
                    return null;
                });

        sSendMsg.invoke(QQCurrentEnv.getKernelMsgservice(), msgId, msgData.contact, msgElements, new HashMap<>(), obj);

    }

    public static void sendPic(String peerUin, String path, int type) throws Exception {
        sendPic(makeContact(peerUin, type), path);
    }

    public static void sendPic(Object contact, String path) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        msgElements.add(CreateElement.createPicElement(path));
        sendMsg(contact, msgElements);
    }

    public static void sendPtt(String peerUin, String path, int type) throws Exception {
        sendPtt(makeContact(peerUin, type), path);
    }

    public static void sendPtt(Object contact, String path) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        msgElements.add(CreateElement.createPttElement(path));
        sendMsg(contact, msgElements);
    }

    public static void sendCard(String peerUin, String data, int type) throws Exception {
        sendCard(makeContact(peerUin, type), data);
    }

    public static void sendCard(Object contact, String data) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        msgElements.add(CreateElement.createArkElement(data));
        sendMsg(contact, msgElements);
    }

    public static void sendVideo(String peerUin, String path, int type) throws Exception {
        sendVideo(makeContact(peerUin, type), path);
    }

    public static void sendVideo(Object contact, String path) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        Object videoElement = CreateElement.createVideoElement(path);
        if (videoElement == null) throw new Exception("视频路径有误或无访问权限");
        msgElements.add(videoElement);
        sendMsg(contact, msgElements);
    }

    public static void sendFile(String peerUin, String path, int type) throws Exception {
        sendFile(makeContact(peerUin, type), path);
    }

    public static void sendFile(Object contact, String path) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        msgElements.add(CreateElement.createFileElement(path));
        sendMsg(contact, msgElements);
    }

    public static void sendReplyMsg(String peerUin, long replyMsgId, String msg, int type) throws Exception {
        sendReplyMsg(makeContact(peerUin, type), replyMsgId, msg);
    }

    public static void sendReplyMsg(Object contact, long replyMsgId, String msg) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        msgElements.add(CreateElement.createReplyElement(replyMsgId));

        List<Object> contentElements = processMessageContent(contact, msg);
        msgElements.addAll(contentElements);

        sendMsg(contact, msgElements);
    }

    // 工具方法
    private static List<Object> processMessageContent(Object contact, String msg) throws Exception {
        List<Object> msgElements = new ArrayList<>();
        List<Map.Entry<String, String>> processedParts = processMessageParts(msg);

        for (Map.Entry<String, String> entry : processedParts) {
            String type = entry.getKey();
            String value = entry.getValue();
            Object element = null;

            switch (type) {
                case "text":
                    element = CreateElement.createTextElement(value);
                    break;
                case "atUin":
                    if (!isPrivateChat(contact)) {
                        int atType = "0".equals(value) ? 1 : 2;
                        String uid = "0".equals(value) ? "0" : QQUtils.getUidFromUin(value);
                        element = CreateElement.createAtTextElement(uid, atType);
                    }
                    break;
                case "pic":
                    if (value.startsWith("/")) {
                        element = CreateElement.createPicElement(value);
                    } else {
                        String filePath = downloadImageToTemp(value.trim());
                        element = CreateElement.createPicElement(filePath);
                    }
                    break;
            }

            if (element != null) msgElements.add(element);
        }

        return msgElements;
    }

    private static boolean isPrivateChat(Object contact) {
        Object chatType = FieldUtils.create(contact).withName("chatType").getValue();
        return "1".equals(chatType.toString());
    }

    private static String downloadImageToTemp(String url) throws Exception {
        File file = DataUtils.createFile("temp", "temp.jpg");
        URL imageUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();

        try (InputStream input = connection.getInputStream();
             FileOutputStream output = new FileOutputStream(file, false)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        } finally {
            connection.disconnect();
        }
        return file.getAbsolutePath();
    }

    private static List<Map.Entry<String, String>> processMessageParts(String input) {
        List<String> parts = splitMessageString(input);
        List<Map.Entry<String, String>> entries = new ArrayList<>();
        Pattern tagPattern = Pattern.compile("\\[(atUin|pic)=([^]]*)]");

        for (String part : parts) {
            if (part.startsWith("[") && part.endsWith("]")) {
                Matcher matcher = tagPattern.matcher(part);
                if (matcher.matches()) {
                    entries.add(new SimpleEntry<>(matcher.group(1), matcher.group(2)));
                } else {
                    entries.add(new SimpleEntry<>("text", part));
                }
            } else {
                entries.add(new SimpleEntry<>("text", part));
            }
        }
        return entries;
    }

    private static List<String> splitMessageString(String input) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[atUin=\\d+]|\\[pic=.*?]");
        Matcher matcher = pattern.matcher(input);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (start > lastEnd) {
                result.add(input.substring(lastEnd, start));
            }

            result.add(input.substring(start, end));
            lastEnd = end;
        }

        if (lastEnd < input.length()) {
            result.add(input.substring(lastEnd));
        }

        return result;
    }

    public static Object makeContact(String peerUin, int type) throws Exception {
        Object peerUid = switch (type) {
            case 1, 100 -> QQUtils.getUidFromUin(peerUin);
            case 2 -> peerUin;
            default -> null;
        };
        return ClassUtils.makeDefaultObject(ClassUtils._Contact(), type, peerUid, "");
    }

    private static long generateMsgUniqueId(int chatType) throws Exception {
        return (long) sGenerateMsgUniqueId.invoke(QQCurrentEnv.getKernelMsgservice(),
                chatType, System.currentTimeMillis());
    }

    public static void getMsgsByMsgId(Object contact, long msgId, Object proxy) throws Exception {
        ArrayList<Long> msgIds = new ArrayList<>();
        msgIds.add(msgId);
        sGetMsgsByMsgId.invoke(QQCurrentEnv.getKernelMsgservice(), contact, msgIds, proxy);
    }

    // CreateElement内部类
    private static class CreateElement {
        private static Object sMsgUtilApiImpl;
        private static Object sQQRecorderUtilsImpl;
        private static Class<?> sJsonClass;
        private static Method sCreateTextElement;
        private static Method sCreateAtTextElement;
        private static Method sCreatePicElement;
        private static Method sCreatePttElement;
        private static Method sGetSampleWaveData;
        private static Method sCreateArkElement;
        private static Method sCreateVideoElement;
        private static Method sCreateReplyElement;
        private static Method sCreateFileElement;
        private static Method sCreateJson;

        private static void initMethod() throws Throwable {
            Class<?> msgUtilApiImplClass = ClassUtils.load("com.tencent.qqnt.msg.api.impl.MsgUtilApiImpl");
            Class<?> qqRecorderUtilsImplClass = ClassUtils.load("com.tencent.mobileqq.ptt.impl.QQRecorderUtilsImpl");
            sJsonClass = DexKit.getClass("CreateElement");

            sMsgUtilApiImpl = ClassUtils.makeDefaultObject(msgUtilApiImplClass);
            sQQRecorderUtilsImpl = ClassUtils.makeDefaultObject(qqRecorderUtilsImplClass);

            sCreateTextElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createTextElement").withParamTypes(String.class).findOne();
            sCreateAtTextElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createAtTextElement").findOne();
            sCreatePicElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createPicElement").withParamTypes(String.class, boolean.class, int.class).findOne();
            sCreatePttElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createPttElement").withParamTypes(String.class, int.class, ArrayList.class).findOne();
            sGetSampleWaveData = MethodUtils.create(qqRecorderUtilsImplClass)
                    .withMethodName("getSampleWaveData").findOne();
            sCreateArkElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createArkElement").findOne();
            sCreateVideoElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createVideoElement").findOne();
            sCreateReplyElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createReplyElement").withParamTypes(long.class).findOne();
            sCreateFileElement = MethodUtils.create(msgUtilApiImplClass)
                    .withMethodName("createFileElement").withParamTypes(String.class).findOne();
            sCreateJson = MethodUtils.create(sJsonClass)
                    .withReturnType(boolean.class).withParamTypes(String.class).findOne();
        }

        private static Object createTextElement(String text) throws Exception {
            return sCreateTextElement.invoke(sMsgUtilApiImpl, text);
        }

        private static Object createAtTextElement(String uid, int atType) throws Exception {
            return sCreateAtTextElement.invoke(sMsgUtilApiImpl, "@全体成员", uid, atType);
        }

        private static Object createPicElement(String path) throws Exception {
            return sCreatePicElement.invoke(sMsgUtilApiImpl, path, true, 0);
        }

        private static Object createPttElement(String path) throws Exception {
            Object waveData = sGetSampleWaveData.invoke(sQQRecorderUtilsImpl, path, 0);
            return sCreatePttElement.invoke(sMsgUtilApiImpl, path, 0, waveData);
        }

        private static Object createArkElement(String data) throws Exception {

            Object json = ClassUtils.makeDefaultObject(sJsonClass);
            sCreateJson.invoke(json, data);
            return sCreateArkElement.invoke(sMsgUtilApiImpl, json);
        }

        private static Object createVideoElement(String path) throws Exception {
            return sCreateVideoElement.invoke(sMsgUtilApiImpl, path);
        }

        private static Object createReplyElement(long msgId) throws Exception {
            return sCreateReplyElement.invoke(sMsgUtilApiImpl, msgId);
        }

        private static Object createFileElement(String path) throws Exception {
            return sCreateFileElement.invoke(sMsgUtilApiImpl, path);
        }
    }
}