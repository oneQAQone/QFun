package me.yxp.qfun.hook.troop;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kotlin.collections.CollectionsKt;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.json.ProtoData;
import me.yxp.qfun.utils.qq.TroopEnableInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.ui.TroopEnableDialog;

@HookItemAnnotation(TAG = "屏蔽艾特全体消息", desc = "屏蔽艾特全体和群待办，点击可选择不需要屏蔽的群聊")
public final class AntiAtAllHook extends BaseWithDataHookItem {
    private static Method sAtAllMethod;
    private static Method sTroopToDoMethod;
    private TroopEnableInfo mTroopEnableInfo;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> notificationCommonInfo = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.NotificationCommonInfo");
        Class<?> recentContactInfo = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.RecentContactInfo");

        sAtAllMethod = MethodUtils.create(ClassUtils._NotificationFacade())
                .withReturnType(void.class)
                .withParamTypes(ClassUtils._AppRuntime(), recentContactInfo, notificationCommonInfo, boolean.class)
                .findOne();

        sTroopToDoMethod = DexKit.getMethod(getNAME());
        return true;
    }

    @Override
    protected void initCallback() {
        mTroopEnableInfo = new TroopEnableInfo("AntiAtAll");

        HookUtils.replaceIfEnable(this, sAtAllMethod, param -> {
            String troopUin = FieldUtils.create(param.args[1]).withName("peerUin").getValue().toString();
            if (FieldUtils.create(param.args[1]).withName("atType").getValue().toString().equals("1")) {
                if (!mTroopEnableInfo.dataList.getIsAvailable(troopUin)) {
                    return null;
                }
            }
            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
        });

        HookUtils.replaceIfEnable(this, sTroopToDoMethod, param -> {
            ArrayList<Byte> byteList = (ArrayList<Byte>) param.args[param.args.length - 1];
            ProtoData data = new ProtoData();
            data.fromBytes(CollectionsKt.toByteArray(byteList));
            String url = data.toJSON().getJSONObject("3").getJSONObject("2").getJSONObject("7").getJSONObject("5").getString("1");
            if (!mTroopEnableInfo.dataList.getIsAvailable(extractUIN(url))) {
                return null;
            }
            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
        });
    }

    @Override
    public void initData() {
        mTroopEnableInfo.initInfo();
    }

    @Override
    public void savaData() {
        mTroopEnableInfo.savaInfo();
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        mTroopEnableInfo.updateInfo();
        new TroopEnableDialog(context, mTroopEnableInfo).show();
    }

    private String extractUIN(String url) throws Throwable {
        if (!url.startsWith("http")) {
            url = "http://example.com/" + url;
        }

        URI uri = new URI(url);
        String query = uri.getQuery();
        if (query == null) {
            throw new IllegalArgumentException("URL中没有查询参数");
        }

        String[] params = query.split("&");
        Map<String, String> paramMap = new HashMap<>();

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                paramMap.put(keyValue[0], keyValue[1]);
            }
        }

        String uin = paramMap.get("uin");
        if (uin == null) {
            throw new IllegalArgumentException("URL中没有uin参数");
        }

        return uin;
    }
}