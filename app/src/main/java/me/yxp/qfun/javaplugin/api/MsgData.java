package me.yxp.qfun.javaplugin.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.yxp.qfun.utils.qq.CookieTool;
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
    public String path;
    public long msgId;
    public Object contact;

    public MsgData(Object msgRecord) throws Exception {
        type = Integer.parseInt(get(FieldUtils.create(msgRecord).withName("chatType").getValue(), 0));
        peerUin = get(FieldUtils.create(msgRecord).withName("peerUin").getValue(), "");
        peerUid = get(FieldUtils.create(msgRecord).withName("peerUid").getValue(), "");
        userUin = get(FieldUtils.create(msgRecord).withName("senderUin").getValue(), "");
        userUid = get(FieldUtils.create(msgRecord).withName("senderUid").getValue(), "");
        time = Long.parseLong(get(FieldUtils.create(msgRecord).withName("msgTime").getValue(), 0));
        msg = "";
        data = msgRecord;
        atList = new ArrayList<>();
        path = "";
        msgId = Long.parseLong(get(FieldUtils.create(msgRecord).withName("msgId").getValue(), 0));
        contact = MsgTool.makeContact(peerUin, type);
        parseMsgElement((List<Object>) FieldUtils.create(msgRecord).withName("elements").getValue());
    }

    private String get(Object obj, Object defaultValue) {
        return (obj == null) ? String.valueOf(defaultValue) : obj.toString();
    }

    private void parseMsgElement(List<Object> msgElements) {
        StringBuilder messageBuilder = new StringBuilder();
        msgType = (int) FieldUtils.create(data).withName("msgType").getValue();

        // 处理文本和@消息
        for (Object msgElement : msgElements) {
            if ("1".equals(get(FieldUtils.create(msgElement).withName("elementType").getValue(), 0))) {
                Object textElement = FieldUtils.create(msgElement).withName("textElement").getValue();
                String atUin = get(FieldUtils.create(textElement).withName("atUid").getValue(), "");
                String content = get(FieldUtils.create(textElement).withName("content").getValue(), "");
                messageBuilder.append(content);

                if (!atUin.equals("0")) {
                    atList.add(atUin);
                }
            }
        }

        // 处理图片和卡片消息
        for (Object msgElement : msgElements) {
            Object picElement = FieldUtils.create(msgElement).withName("picElement").getValue();
            if (picElement != null) {
                String picUrl = get(FieldUtils.create(picElement).withName("originImageUrl").getValue(), "");
                if (!picUrl.isEmpty()) {
                    String rkey = (type == 1) ? CookieTool.getFriendRKey() : CookieTool.getGroupRKey();
                    messageBuilder.append("[pic=https://multimedia.nt.qq.com.cn").append(picUrl).append(rkey).append("]");
                }
            }

            Object arkElement = FieldUtils.create(msgElement).withName("arkElement").getValue();
            if (arkElement != null) {
                String json = get(FieldUtils.create(arkElement).withName("bytesData").getValue(), "{}");
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
                    String filePath = get(FieldUtils.create(obj).withName("filePath").getValue(), "");
                    path += filePath;
                } catch (Exception e) {
                    // 忽略字段访问异常
                }
            }
        }
    }
}