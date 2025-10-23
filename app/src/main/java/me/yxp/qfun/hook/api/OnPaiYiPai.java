package me.yxp.qfun.hook.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.json.ProtoData;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnPaiYiPai extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnPaiYiPai();
    private static final String SERVICE_CMD = "trpc.msg.olpush.OlPushService.MsgPush";
    private static final String QQ_NUMBER_REGEX = "[1-9]\\d{4,12}";

    @Override
    public void loadHook() throws Throwable {
        Method onReceive = MethodUtils.create(ClassUtils._MSFServlet())
                .withMethodName("onReceive")
                .withParamTypes(ClassUtils._FromServiceMsg())
                .findOne();

        HookUtils.hookAlways(onReceive, null, param -> {
            Object fromServiceMsg = param.args[0];
            String serviceCmd = FieldUtils.create(fromServiceMsg)
                    .withName("serviceCmd")
                    .getValue()
                    .toString();

            if (!SERVICE_CMD.equals(serviceCmd)) {
                return;
            }

            byte[] wupBuffer = (byte[]) FieldUtils.create(fromServiceMsg)
                    .withName("wupBuffer")
                    .getValue();
            ProtoData data = new ProtoData();
            data.fromBytes(wupBuffer);
            JSONObject json = data.toJSON();
            JSONObject json1 = json.getJSONObject("1").getJSONObject("1");
            JSONObject json2 = json.getJSONObject("1").getJSONObject("2");

            String peerUin = json1.get("1").toString();
            int chatType;
            String fromUin;
            String toUin;

            if (json2.getInt("1") == 732 && json2.getInt("2") == 20 && json.getJSONObject("1").has("3")) {
                chatType = 2;
                fromUin = extractQQ(json, "1");
                toUin = extractQQ(json, "2");
            } else if (json2.getInt("1") == 528 && json2.getInt("2") == 290 && json.getJSONObject("1").has("3")) {
                chatType = 1;
                fromUin = peerUin;
                toUin = extractToUinFromArray(json);
            } else {
                return;
            }

            if (!isValidQQNumber(fromUin) || !toUin.equals(QQCurrentEnv.getCurrentUin())) {
                return;
            }

            for (Listener listener : mListenerSet) {
                if (listener instanceof PaiYiPaiListener) {
                    ((PaiYiPaiListener) listener).onPai(peerUin, chatType, fromUin);
                }
            }
        });
    }

    private String extractQQ(JSONObject json, String type) throws Throwable {
        String targetString = json.getJSONObject("1").getJSONObject("3").getString("2");
        int startIndex = targetString.indexOf("uin_str" + type) + 8;

        while (startIndex < targetString.length() && !Character.isDigit(targetString.charAt(startIndex))) {
            startIndex++;
        }

        int endIndex = targetString.indexOf(':', startIndex);
        return targetString.substring(startIndex, endIndex);
    }

    private String extractToUinFromArray(JSONObject json) throws Throwable {
        JSONObject innerObj = json.getJSONObject("1").getJSONObject("3").getJSONObject("2");
        JSONArray dataArray = innerObj.getJSONArray("7");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            if (item.has("1") && "uin_str2".equals(item.getString("1"))) {
                return item.getString("2");
            }
        }
        return null;
    }

    private boolean isValidQQNumber(String input) {
        return input != null && input.matches(QQ_NUMBER_REGEX);
    }

    public interface PaiYiPaiListener extends Listener {
        void onPai(String peerUin, int chatType, String fromUin);
    }
}