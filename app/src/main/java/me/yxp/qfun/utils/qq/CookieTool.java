package me.yxp.qfun.utils.qq;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import me.yxp.qfun.hook.api.OnRKey;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class CookieTool {
    private static Class<?> sTicketManagerImpl;
    private static Class<?> sPskeyManagerImpl;
    private static Class<?> sTroopHWApiImpl;
    private static Class<?> sKeyVar;

    private static Method sGetRealSkey;
    private static Method sGetSkey;
    private static Method sGetStweb;
    private static Method sGetPt4Token;
    private static Method sGetPskeyFromLocal;
    private static Method sOnCreate;
    private static Method sToReturnType;
    private static Method sGetBkn;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("CookieTool", th);
        }
    }

    private static void initMethod() throws Throwable {
        sTicketManagerImpl = ClassUtils.load("mqq.app.TicketManagerImpl");
        sTroopHWApiImpl = ClassUtils.load("com.tencent.mobileqq.troop.api.impl.TroopHWApiImpl");
        sPskeyManagerImpl = ClassUtils.load("com.tencent.mobileqq.pskey.api.impl.PskeyManagerImpl");
        sKeyVar = DexKit.getClass("CookieTool");


        sGetSkey = MethodUtils.create(sTicketManagerImpl)
                .withMethodName("getSkey")
                .withParamTypes(String.class)
                .findOne();
        sGetRealSkey = MethodUtils.create(sTicketManagerImpl)
                .withMethodName("getRealSkey")
                .findOne();
        sGetStweb = MethodUtils.create(sTicketManagerImpl)
                .withMethodName("getStweb")
                .findOne();
        sGetPt4Token = MethodUtils.create(sTicketManagerImpl)
                .withMethodName("getPt4Token")
                .findOne();
        sGetPskeyFromLocal = MethodUtils.create(sPskeyManagerImpl)
                .withMethodName("getPskeyFromLocal")
                .findOne();
        sOnCreate = MethodUtils.create(sPskeyManagerImpl)
                .withMethodName("onCreate")
                .findOne();
        sToReturnType = MethodUtils.create(sPskeyManagerImpl)
                .withMethodName("toReturnType")
                .findOne();
        sGetBkn = MethodUtils.create(sTroopHWApiImpl)
                .withMethodName("getBknBySkey")
                .findOne();
    }

    public static String getRealSkey() throws Exception {
        Object ticketManagerImpl = ClassUtils.makeDefaultObject(sTicketManagerImpl, QQCurrentEnv.getQQAppInterface());
        return sGetRealSkey.invoke(ticketManagerImpl, QQCurrentEnv.getCurrentUin()).toString();
    }

    public static String getSkey() throws Exception {
        Object ticketManagerImpl = ClassUtils.makeDefaultObject(sTicketManagerImpl, QQCurrentEnv.getQQAppInterface());
        return sGetSkey.invoke(ticketManagerImpl, QQCurrentEnv.getCurrentUin()).toString();
    }

    public static String getStweb() throws Exception {
        Object ticketManagerImpl = ClassUtils.makeDefaultObject(sTicketManagerImpl, QQCurrentEnv.getQQAppInterface());
        return sGetStweb.invoke(ticketManagerImpl, QQCurrentEnv.getCurrentUin()).toString();
    }

    public static String getGTK(String url) throws Exception {
        String pskey = getPskey(url);
        int hash = 5381;
        for (int i = 0; i < pskey.length(); i++) {
            hash += (hash << 5) + pskey.charAt(i);
        }
        return String.valueOf(Integer.MAX_VALUE & hash);
    }

    public static String getPskey(String url) throws Exception {

        Object pskeyManagerImpl = ClassUtils.makeDefaultObject(sPskeyManagerImpl);

        sOnCreate.invoke(pskeyManagerImpl, QQCurrentEnv.getQQAppInterface());
        Map<String, Object> map1 = new HashMap<>();
        map1.put(url, ClassUtils.makeDefaultObject(sKeyVar, "", "", 0));

        Object obj = sGetPskeyFromLocal.invoke(pskeyManagerImpl, QQCurrentEnv.getCurrentUin(), map1);
        Map<String, String> map2 = (Map<String, String>) sToReturnType.invoke(pskeyManagerImpl, obj, 0);

        return map2.getOrDefault(url, null);
    }

    public static String getPt4Token(String url) throws Exception {
        Object ticketManagerImpl = ClassUtils.makeDefaultObject(sTicketManagerImpl, QQCurrentEnv.getQQAppInterface());
        Object pt4Token = sGetPt4Token.invoke(ticketManagerImpl, QQCurrentEnv.getCurrentUin(), url);
        return pt4Token != null ? pt4Token.toString() : null;
    }

    public static long getBkn(String key) throws Exception {
        return Long.parseLong(sGetBkn.invoke(ClassUtils.makeDefaultObject(sTroopHWApiImpl), key).toString());
    }

    public static String getFriendRKey() {
        return ((OnRKey) OnRKey.INSTANCE).friendRkey;
    }

    public static String getGroupRKey() {
        return ((OnRKey) OnRKey.INSTANCE).groupRkey;
    }
}