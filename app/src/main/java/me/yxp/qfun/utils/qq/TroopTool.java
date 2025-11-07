package me.yxp.qfun.utils.qq;

import android.content.Intent;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class TroopTool {
    private static final String[] ROLES = new String[]{"MEMBER", "ADMIN", "OWNER"};

    private static Class<?> sTroopListRepoApiImpl;
    private static Class<?> sMemberSettingHandler;
    private static Class<?> sMemberSettingGroupManagePart;
    private static Class<?> sEditUniqueTitleActivity;
    private static Class<?> sTroopMemberCardHandler;
    private static Class<?> sTroopMemberCardInfo;
    private static Class<?> sTroopMemberListRepo;
    private static Class<?> sTroopOperationRepo;
    private static Class<?> sTroopInfoServiceImpl;
    private static Class sMemberRole;

    private static Method sGetTroopList;
    private static Method sShutUp;
    private static Method sKickGroup;
    private static Method sSetGroupAdmin;
    private static Method sSetGroupMemberTitle;
    private static Method sChangeMemberName;
    private static Method sFetchTroopMemberList;
    private static Method sShutUpAll;
    private static Method sFetchTroopMemberInfo;
    private static Method sGetTroopInfo;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("TroopTool", th);
        }
    }

    private static void initMethod() throws Throwable {
        sTroopListRepoApiImpl = ClassUtils.load("com.tencent.qqnt.troop.impl.TroopListRepoApiImpl");
        sMemberSettingHandler = ClassUtils.load("com.tencent.mobileqq.troop.membersetting.handler.MemberSettingHandler");
        try {
            sMemberSettingGroupManagePart = ClassUtils.load("com.tencent.mobileqq.troop.membersetting.part.MemberSettingGroupManagePart");
        } catch (Exception e) {
            sMemberSettingGroupManagePart = DexKit.getClass("TroopTool");
        }
        sEditUniqueTitleActivity = ClassUtils.load("com.tencent.biz.troop.EditUniqueTitleActivity");
        sTroopMemberCardHandler = ClassUtils.load("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler");
        sTroopMemberCardInfo = ClassUtils.load("com.tencent.mobileqq.data.troop.TroopMemberCardInfo");
        sTroopMemberListRepo = ClassUtils.load("com.tencent.qqnt.troopmemberlist.TroopMemberListRepo");
        sTroopOperationRepo = ClassUtils.load("com.tencent.qqnt.troop.TroopOperationRepo");
        sTroopInfoServiceImpl = ClassUtils.load("com.tencent.mobileqq.troop.api.impl.TroopInfoServiceImpl");
        sMemberRole = ClassUtils.load("com.tencent.qqnt.kernelpublic.nativeinterface.MemberRole");

        sGetTroopList = MethodUtils.create(sTroopListRepoApiImpl)
                .withMethodName("getSortedJoinedTroopInfoFromCache")
                .findOne();
        try {
            sShutUp = MethodUtils.create(sMemberSettingHandler)
                    .withReturnType(boolean.class)
                    .withParamTypes(String.class, String.class, long.class)
                    .findOne();
        } catch (Exception e) {
            sShutUp = MethodUtils.create(sMemberSettingHandler)
                    .withReturnType(boolean.class)
                    .withParamTypes(long.class, String.class, String.class)
                    .findOne();
        }
        try {
            sKickGroup = MethodUtils.create(sMemberSettingHandler)
                    .withReturnType(void.class)
                    .withParamTypes(long.class, List.class, boolean.class, boolean.class)
                    .findOne();
        } catch (Exception e) {
            sKickGroup = MethodUtils.create(sMemberSettingHandler)
                    .withReturnType(void.class)
                    .withParamTypes(long.class, boolean.class, boolean.class, List.class)
                    .findOne();
        }
        sSetGroupAdmin = MethodUtils.create(sMemberSettingGroupManagePart)
                .withReturnType(void.class)
                .withParamTypes(byte.class, String.class, String.class)
                .findOne();
        try {
            sSetGroupMemberTitle = MethodUtils.create(sEditUniqueTitleActivity)
                    .withReturnType(void.class)
                    .withParamTypes(ClassUtils._QQAppInterface(), String.class, String.class, String.class,
                            ClassUtils.load("mqq.observer.BusinessObserver"))
                    .findOne();
        } catch (Exception e) {
            sSetGroupMemberTitle = MethodUtils.create(sEditUniqueTitleActivity)
                    .withReturnType(void.class)
                    .withParamTypes(String.class, String.class, String.class)
                    .findOne();
        }
        sChangeMemberName = MethodUtils.create(sTroopMemberCardHandler)
                .withReturnType(void.class)
                .withParamTypes(String.class, ArrayList.class, ArrayList.class)
                .findOne();
        sFetchTroopMemberList = MethodUtils.create(sTroopMemberListRepo)
                .withMethodName("fetchTroopMemberList")
                .withParamCount(5)
                .findOne();
        sShutUpAll = MethodUtils.create(sTroopOperationRepo)
                .withMethodName("modifyTroopShutUpTime")
                .findOne();
        sFetchTroopMemberInfo = MethodUtils.create(sTroopMemberListRepo)
                .withMethodName("fetchTroopMemberInfo")
                .withParamCount(6)
                .findOne();
        sGetTroopInfo = MethodUtils.create(sTroopInfoServiceImpl)
                .withMethodName("getTroopInfo")
                .withParamTypes(String.class)
                .findOne();
    }

    public static List<HashMap<String, Object>> getGroupList() throws Exception {
        List<HashMap<String, Object>> groupList = new ArrayList<>();
        Object troopListRepoApiImpl = ClassUtils.makeDefaultObject(sTroopListRepoApiImpl);

        List<Object> troopInfoList = new ArrayList<>();
        while (troopInfoList.isEmpty()) {
            troopInfoList = (List<Object>) sGetTroopList.invoke(troopListRepoApiImpl);
        }

        for (Object troopInfo : troopInfoList) {
            HashMap<String, Object> troopMap = new HashMap<>();
            troopMap.put("group", FieldUtils.create(troopInfo).withName("troopuin").getValue().toString());
            troopMap.put("groupName", FieldUtils.create(troopInfo).withName("troopNameFromNT").getValue().toString());
            troopMap.put("groupOwner", FieldUtils.create(troopInfo).withName("troopowneruin").getValue().toString());
            troopMap.put("groupInfo", troopInfo);
            groupList.add(troopMap);
        }

        return groupList;
    }

    private static List<Object> getMemberInfoList(String troopUin) throws Exception {
        CompletableFuture<ArrayList<Object>> completableFuture = new CompletableFuture<>();
        Object troopMemberListRepo = ClassUtils.makeDefaultObject(sTroopMemberListRepo);

        Object callback = Proxy.newProxyInstance(ClassUtils.getHostClassLoader(),
                new Class<?>[]{sFetchTroopMemberList.getParameterTypes()[4]}, (proxy, method, args) -> {
                    if (method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 2) {
                        completableFuture.complete((ArrayList<Object>) args[1]);
                        return null;
                    }
                    return method.invoke(troopMemberListRepo, args);
                });

        sFetchTroopMemberList.invoke(troopMemberListRepo, troopUin, null, true, "", callback);
        return completableFuture.get(5, TimeUnit.SECONDS);
    }

    public static List<HashMap<String, Object>> getGroupMemberList(String troopUin) throws Exception {
        List<HashMap<String, Object>> groupMemberList = new ArrayList<>();

        for (Object memberInfo : getMemberInfoList(troopUin)) {
            HashMap<String, Object> memberInfoMap = new HashMap<>();
            memberInfoMap.put("joinGroupTime", FieldUtils.create(memberInfo).withName("join_time").getValue());
            memberInfoMap.put("lastActiveTime", FieldUtils.create(memberInfo).withName("last_active_time").getValue());
            memberInfoMap.put("uin", FieldUtils.create(memberInfo).withName("memberuin").getValue().toString());
            memberInfoMap.put("uinLevel", FieldUtils.create(memberInfo).withName("realLevel").getValue());

            String uinName = FieldUtils.create(memberInfo).withName("troopnick").getValue().toString();
            if (uinName.isEmpty()) {
                uinName = FieldUtils.create(memberInfo).withName("friendnick").getValue().toString();
            }
            memberInfoMap.put("uinName", uinName);
            memberInfoMap.put("memberInfo", memberInfo);

            for (String role : ROLES) {
                if (FieldUtils.create(memberInfo).withName("role").getValue() == Enum.valueOf(sMemberRole, role)) {
                    memberInfoMap.put("role", role);
                }
            }
            groupMemberList.add(memberInfoMap);
        }

        return groupMemberList;
    }

    public static List<HashMap<String, Object>> getProhibitList(String troopUin) throws Exception {
        List<HashMap<String, Object>> prohibitList = new ArrayList<>();

        for (Object memberInfo : getMemberInfoList(troopUin)) {
            long gagTimeStamp = (long) FieldUtils.create(memberInfo).withName("gagTimeStamp").getValue();
            long currentTime = System.currentTimeMillis() / 1000;

            if (gagTimeStamp > currentTime) {
                HashMap<String, Object> prohibitMap = new HashMap<>();
                prohibitMap.put("user", FieldUtils.create(memberInfo).withName("memberuin").getValue().toString());
                prohibitMap.put("endTime", gagTimeStamp);
                prohibitMap.put("time", gagTimeStamp - currentTime);

                String userName = FieldUtils.create(memberInfo).withName("troopnick").getValue().toString();
                if (userName.isEmpty()) {
                    userName = FieldUtils.create(memberInfo).withName("friendnick").getValue().toString();
                }
                prohibitMap.put("userName", userName);
                prohibitList.add(prohibitMap);
            }
        }

        return prohibitList;
    }

    public static Object getTroopInfo(String troopUin) throws Exception {
        Object troopInfoServiceImpl = ClassUtils.makeDefaultObject(sTroopInfoServiceImpl);
        return sGetTroopInfo.invoke(troopInfoServiceImpl, troopUin);
    }

    public static Object getMemberInfo(String troopUin, String uin) throws Exception {
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        Object troopMemberListRepo = ClassUtils.makeDefaultObject(sTroopMemberListRepo);

        Object callback = Proxy.newProxyInstance(ClassUtils.getHostClassLoader(),
                new Class<?>[]{sFetchTroopMemberInfo.getParameterTypes()[5]}, (proxy, method, args) -> {
                    if (method.getReturnType() == Void.TYPE &&
                            method.getParameterTypes()[0] == ClassUtils.load("com.tencent.mobileqq.data.troop.TroopMemberInfo")) {
                        completableFuture.complete(args[0]);
                        return null;
                    }
                    return method.invoke(troopMemberListRepo, args);
                });

        sFetchTroopMemberInfo.invoke(troopMemberListRepo, troopUin, uin, true, null, "", callback);
        return completableFuture.get(5, TimeUnit.SECONDS);
    }

    public static String getMemberName(String troopUin, String uin) {
        try {
            Object memberInfo = getMemberInfo(troopUin, uin);
            String userName = FieldUtils.create(memberInfo).withName("troopnick").getValue().toString();
            if (userName.isEmpty()) {
                userName = FieldUtils.create(memberInfo).withName("friendnick").getValue().toString();
            }
            return userName;
        } catch (Exception e) {
            return uin;
        }
    }

    public static void shutUp(String troopUin, String uin, long time) throws Exception {

        Object memberSettingHandler = ClassUtils.makeDefaultObject(sMemberSettingHandler, QQCurrentEnv.getQQAppInterface());

        try {
            sShutUp.invoke(memberSettingHandler, troopUin, uin, time);
        } catch (Exception e) {
            sShutUp.invoke(memberSettingHandler, time, troopUin, uin);
        }

    }

    public static void shutUpAll(String troopUin, boolean enable) throws Exception {
        long code = enable ? 268435455 : 0;
        Object troopOperationRepo = ClassUtils.makeDefaultObject(sTroopOperationRepo);
        sShutUpAll.invoke(troopOperationRepo, troopUin, code, null, null);
    }

    public static boolean isShutUp(String troopUin) throws Exception {
        Object troopInfo = getTroopInfo(troopUin);
        long dwGagTimeStamp = (long) FieldUtils.create(troopInfo).withName("dwGagTimeStamp").getValue();
        long dwGagTimeStampMe = (long) FieldUtils.create(troopInfo).withName("dwGagTimeStamp_me").getValue();
        return !(dwGagTimeStamp == 0 && dwGagTimeStampMe == 0);
    }

    public static void setGroupAdmin(String troopUin, String uin, boolean enable) throws Exception {
        byte adminFlag = enable ? (byte) 1 : (byte) 0;
        sSetGroupAdmin.invoke(ClassUtils.makeDefaultObject(sMemberSettingGroupManagePart),
                adminFlag, troopUin, uin);
    }

    public static void kickGroup(String troopUin, String uin, boolean block) throws Exception {
        ArrayList<Long> uinList = new ArrayList<>();
        uinList.add(Long.valueOf(uin));

        Object memberSettingHandler = ClassUtils.makeDefaultObject(sMemberSettingHandler, QQCurrentEnv.getQQAppInterface());

        try {
            sKickGroup.invoke(memberSettingHandler, Long.valueOf(troopUin), uinList, block, false);
        } catch (Exception e) {
            sKickGroup.invoke(memberSettingHandler, Long.valueOf(troopUin), block, false, uinList);
        }
    }

    public static void setGroupMemberTitle(String troopUin, String uin, String title) throws Exception {

        Object editUniqueTitleActivity = ClassUtils.makeDefaultObject(sEditUniqueTitleActivity);

        try {
            sSetGroupMemberTitle.invoke(editUniqueTitleActivity, QQCurrentEnv.getQQAppInterface(), troopUin, uin, title, null);
        } catch (Exception e) {

            XposedHelpers.setObjectField(editUniqueTitleActivity, "app", QQCurrentEnv.getQQAppInterface());
            XposedHelpers.callMethod(editUniqueTitleActivity, "setIntent", new Intent());
            sSetGroupMemberTitle.invoke(editUniqueTitleActivity, troopUin, uin, title);
        }
    }

    public static void changeMemberName(String troopUin, String uin, String name) throws Exception {
        Object troopMemberCardInfo = ClassUtils.makeDefaultObject(sTroopMemberCardInfo);
        FieldUtils.create(troopMemberCardInfo).withName("colorNick").setValue("");
        FieldUtils.create(troopMemberCardInfo).withName("colorNickId").setValue(0);
        FieldUtils.create(troopMemberCardInfo).withName("memberuin").setValue(uin);
        FieldUtils.create(troopMemberCardInfo).withName("troopnick").setValue(name);
        FieldUtils.create(troopMemberCardInfo).withName("name").setValue(name);
        FieldUtils.create(troopMemberCardInfo).withName("troopuin").setValue(troopUin);

        List<Object> cardInfoList = new ArrayList<>();
        cardInfoList.add(troopMemberCardInfo);

        List<Integer> fieldList = new ArrayList<>();
        fieldList.add(1);

        sChangeMemberName.invoke(ClassUtils.makeDefaultObject(sTroopMemberCardHandler, QQCurrentEnv.getQQAppInterface()),
                troopUin, cardInfoList, fieldList);
    }
}

