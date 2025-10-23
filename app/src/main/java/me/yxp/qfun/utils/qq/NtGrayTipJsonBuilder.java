package me.yxp.qfun.utils.qq;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class NtGrayTipJsonBuilder {
    public static final int AIO_AV_C2C_NOTICE = 2021;
    public static final int AIO_AV_GROUP_NOTICE = 2022;

    private final ArrayList<Item> mItems = new ArrayList<>(4);

    public NtGrayTipJsonBuilder() {
    }

    public static void addLocalGrayTipMsg(Object contact, String jsonStr, long busiId) throws Throwable {
        Class<?> jsonGrayElement = ClassUtils.load("com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement");
        Object grayElement = ClassUtils.makeDefaultObject(jsonGrayElement, busiId, jsonStr, "", false, null);

        Method addLocalJsonGrayTipMsg = MethodUtils.create(ClassUtils._IKernelMsgService$CppProxy())
                .withMethodName("addLocalJsonGrayTipMsg")
                .findOne();

        addLocalJsonGrayTipMsg.invoke(QQCurrentEnv.getKernelMsgservice(), contact, grayElement, true, true, null);
    }

    public NtGrayTipJsonBuilder appendText(String text) {
        mItems.add(new TextItem(text));
        return this;
    }

    public NtGrayTipJsonBuilder append(Item item) {
        mItems.add(item);
        return this;
    }

    public JSONObject build() {
        try {
            JSONObject json = new JSONObject();
            json.put("align", "center");

            JSONArray items = new JSONArray();
            for (Item item : mItems) {
                items.put(item.toJson());
            }
            json.put("items", items);

            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Item {
        JSONObject toJson() throws JSONException;
    }

    public static class TextItem implements Item {
        private final String mText;

        public TextItem(String text) {
            Objects.requireNonNull(text);
            mText = text;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("txt", mText);
            json.put("type", "nor");
            return json;
        }

        @NonNull
        @Override
        public String toString() {
            try {
                return toJson().toString();
            } catch (JSONException e) {
                return super.toString();
            }
        }
    }

    public static class UserItem implements Item {
        private final String mUin;
        private final String mUid;
        private final String mNick;

        public UserItem(String uin, String uid, String nick) {
            mUin = uin;
            mUid = uid;
            mNick = Objects.requireNonNull(nick);
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("col", "3");
            json.put("jp", mUid);
            json.put("nm", mNick);
            json.put("tp", "0");
            json.put("type", "qq");
            json.put("uid", mUid);
            json.put("uin", mUin);
            return json;
        }

        @NonNull
        @Override
        public String toString() {
            try {
                return toJson().toString();
            } catch (JSONException e) {
                return super.toString();
            }
        }
    }

    public static class MsgRefItem implements Item {
        private final String mText;
        private final long mMsgSeq;

        public MsgRefItem(String text, long msgSeq) {
            mText = Objects.requireNonNull(text);
            mMsgSeq = msgSeq;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("type", "url");
            json.put("txt", mText);
            json.put("col", "3");
            json.put("local_jp", 58);

            JSONObject param = new JSONObject();
            param.put("seq", mMsgSeq);
            json.put("param", param);

            return json;
        }

        @NonNull
        @Override
        public String toString() {
            try {
                return toJson().toString();
            } catch (JSONException e) {
                return super.toString();
            }
        }
    }
}