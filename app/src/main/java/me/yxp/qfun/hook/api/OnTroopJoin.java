package me.yxp.qfun.hook.api;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import kotlin.collections.CollectionsKt;
import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.json.ProtoData;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public final class OnTroopJoin extends ApiHookItem {
    public static final ApiHookItem INSTANCE = new OnTroopJoin();

    @Override
    public void loadHook() throws Throwable {
        if (HostInfo.isTIM()) {
            Method handleMemberJoin = DexKit.getMethod("OnTroopJoin");
            HookUtils.hookAlways(handleMemberJoin, null, param -> handleJoin((String) param.args[0], (String) param.args[1]));
        }

        if (HostInfo.isQQ()) {
            Class<?> troopMemberAddPushProcessor = ClassUtils
                    .load("com.tencent.qqnt.push.processor.TroopMemberAddPushProcessor");
            Method joinGroup = MethodUtils.create(troopMemberAddPushProcessor)
                    .withReturnType(void.class)
                    .withParamTypes(ArrayList.class)
                    .findOne();

            HookUtils.hookAlways(joinGroup, null, param -> {
                ArrayList<Byte> byteList = (ArrayList<Byte>) param.args[0];
                ProtoData data = new ProtoData();
                data.fromBytes(CollectionsKt.toByteArray(byteList));
                JSONObject json = data.toJSON();

                String troopUin = String.valueOf(json.getJSONObject("1").getLong("1"));
                String memberUid = json.getJSONObject("3").getJSONObject("2").getString("3");
                String memberUin = QQUtils.getUinFromUid(memberUid);
                handleJoin(troopUin, memberUin);
            });
        }


    }

    private void handleJoin(String troopUin, String memberUin) throws Throwable {
        for (Listener listener : mListenerSet) {
            if (listener instanceof TroopJoinListener) {
                ((TroopJoinListener) listener).onJoin(troopUin, memberUin);
            }
        }
    }

    public interface TroopJoinListener extends Listener {
        void onJoin(String troopUin, String memberUin) throws Throwable;
    }
}