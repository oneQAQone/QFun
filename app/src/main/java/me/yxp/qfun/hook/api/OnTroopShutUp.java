package me.yxp.qfun.hook.api;

import org.json.JSONObject;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.json.ProtoData;
import me.yxp.qfun.utils.qq.QQUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnTroopShutUp extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnTroopShutUp();
    private static final String SERVICE_CMD = "trpc.msg.olpush.OlPushService.MsgPush";

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
            JSONObject json1 = json.getJSONObject("1").getJSONObject("2");

            if (!(json1.getInt("1") == 732 && json1.getInt("2") == 12)) {
                return;
            }

            JSONObject json2 = json.getJSONObject("1").getJSONObject("3").getJSONObject("2");
            String troopUin = String.valueOf(json2.getLong("1"));
            String opUin = QQUtils.getUinFromUid(json2.getString("4"));
            String memberUin = QQUtils.getUinFromUid(json2.getJSONObject("5").getJSONObject("3").getString("1"));
            long time = json2.getJSONObject("5").getJSONObject("3").getLong("2");

            for (Listener listener : mListenerSet) {
                if (listener instanceof TroopShutUpListener) {
                    ((TroopShutUpListener) listener).onShutUp(troopUin, memberUin, time, opUin);
                }
            }
        });
    }

    public interface TroopShutUpListener extends Listener {
        void onShutUp(String troopUin, String memberUin, long time, String opUin) throws Throwable;
    }
}