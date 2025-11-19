package me.yxp.qfun.javaplugin.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.yxp.qfun.utils.qq.CookieTool;
import me.yxp.qfun.utils.qq.FriendTool;
import me.yxp.qfun.utils.qq.MsgTool;
import me.yxp.qfun.utils.reflect.FieldUtils;

public class MsgData {
    public int type;
    public int msgType;
    public String peerUin;
    public String peerUid;
    public String userUin;
    public String userUid;
    public long time;
    public String msg;
    public Object data;
    public ArrayList<String> atList;
    public Map<String, String> atMap;
    public String path;
    public long msgId;
    public Object contact;

    public MsgData(Object msgRecord) throws Exception {
        type = (int) Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("chatType").getValue(), 0);
        peerUin = String.valueOf(Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("peerUin").getValue(), ""));
        peerUid = (String) Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("peerUid").getValue(), "");
        userUin = String.valueOf(Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("senderUin").getValue(), ""));
        userUid = (String) Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("senderUid").getValue(), "");
        time = (long) Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("msgTime").getValue(), 0);
        msg = "";
        data = msgRecord;
        atList = new ArrayList<>();
        atMap = new HashMap<>();
        path = "";
        msgId = (long) Objects.requireNonNullElse(FieldUtils.create(msgRecord).withName("msgId").getValue(), 0);
        contact = MsgTool.makeContact(peerUin, type);
        parseMsgElement((List<Object>) FieldUtils.create(msgRecord).withName("elements").getValue());
    }

    private void parseMsgElement(List<Object> msgElements) {
        StringBuilder messageBuilder = new StringBuilder();
        msgType = (int) FieldUtils.create(data).withName("msgType").getValue();

        // 处理文本和@消息
        for (Object msgElement : msgElements) {
            if (1 == (int) Objects.requireNonNullElse(FieldUtils.create(msgElement).withName("elementType").getValue(), 0)) {
                Object textElement = FieldUtils.create(msgElement).withName("textElement").getValue();
                int atType = (int) Objects.requireNonNullElse(FieldUtils.create(textElement).withName("atType").getValue(), 0);
                String atNtUid = String.valueOf(Objects.requireNonNullElse(FieldUtils.create(textElement).withName("atNtUid").getValue(), "0"));
                String atUid = String.valueOf(Objects.requireNonNullElse(FieldUtils.create(textElement).withName("atUid").getValue(), "0"));
                String content = (String) Objects.requireNonNullElse(FieldUtils.create(textElement).withName("content").getValue(), "");
                messageBuilder.append(content);

                if (atType == 2) {
                    try {
                        String atUin = FriendTool.getUinFromUid(atNtUid);
                        if (atUin.isEmpty()) atUin = atUid;
                        atList.add(atUin);
                        atMap.put(atUin, content);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // 处理图片和卡片消息
        String rkey = (type == 1) ? CookieTool.getFriendRKey() : CookieTool.getGroupRKey();
        for (Object msgElement : msgElements) {
            Object picElement = FieldUtils.create(msgElement).withName("picElement").getValue();
            if (picElement != null) {
                String picUrl = (String) Objects.requireNonNullElse(FieldUtils.create(picElement).withName("originImageUrl").getValue(), "");
                if (!picUrl.isEmpty()) {
                    messageBuilder.append("[pic=https://multimedia.nt.qq.com.cn").append(picUrl).append(rkey).append("]");
                }
            }

            Object arkElement = FieldUtils.create(msgElement).withName("arkElement").getValue();
            if (arkElement != null) {
                String json = (String) Objects.requireNonNullElse(FieldUtils.create(arkElement).withName("bytesData").getValue(), "{}");
                if (!json.equals("{}")) {
                    messageBuilder.append(json);
                }
            }
        }

        msg = messageBuilder.toString();

        // 提取文件路径
        for (Object msgElement : msgElements) {
            for (Field field : msgElement.getClass().getDeclaredFields()) {
                try {
                    Object obj = field.get(msgElement);
                    String filePath = (String) Objects.requireNonNullElse(FieldUtils.create(obj).withName("filePath").getValue(), "");
                    path += filePath;
                } catch (Exception e) {
                    // 忽略字段访问异常
                }
            }
        }
    }
}