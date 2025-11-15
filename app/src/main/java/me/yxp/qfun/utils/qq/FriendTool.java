package me.yxp.qfun.utils.qq;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class FriendTool {
    private static Class<?> sCardHandler;

    private static Object FriendsInfoServiceImpl;

    private static Method sSendZan;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("FriendTool", th);
        }
    }

    private static void initMethod() throws Throwable {
        Class<?> FriendsInfoService = ClassUtils.load("com.tencent.qqnt.ntrelation.friendsinfo.api.impl.FriendsInfoServiceImpl");
        FriendsInfoServiceImpl = ClassUtils.makeDefaultObject(FriendsInfoService);
        sCardHandler = ClassUtils._CardHandler();

        sSendZan = MethodUtils.create(sCardHandler)
                .withReturnType(void.class)
                .withParamTypes(long.class, long.class, byte[].class, int.class, int.class, int.class)
                .findOne();
    }

    public static List<HashMap<String, Object>> getAllFriend() throws Exception {

        List<HashMap<String, Object>> friendList = new ArrayList<>();

        List<Object> friendInfoList = (List<Object>) XposedHelpers.callMethod(FriendsInfoServiceImpl, "getAllFriend", new Class[]{String.class}, "");
        for (Object friendInfo : friendInfoList) {
            HashMap<String, Object> infoMap = new HashMap<>();

            String[] values = friendInfo.toString().split(" ");
            String uin = values[2];
            String uid = values[4];
            String name = (String) XposedHelpers.callMethod(FriendsInfoServiceImpl, "getNickWithUid", uid, "");
            String remark = (String) XposedHelpers.callMethod(FriendsInfoServiceImpl, "getRemarkWithUid", uid, "");

            infoMap.put("uin", Objects.requireNonNullElse(uin, ""));
            infoMap.put("uid", Objects.requireNonNullElse(uid, ""));
            infoMap.put("name", Objects.requireNonNullElse(name, ""));
            infoMap.put("remark", Objects.requireNonNullElse(remark, ""));

            friendList.add(infoMap);
        }

        return friendList;

    }

    public static boolean isFriend(String uin) throws Exception {
        String uid = getUidFromUin(uin);
        return (boolean) XposedHelpers.callMethod(FriendsInfoServiceImpl, "isFriend", uid, null);
    }

    public static String getUidFromUin(String uin) throws Exception {
        return (String) XposedHelpers.callMethod(FriendsInfoServiceImpl, "getUidFromUin", uin);
    }

    public static String getUinFromUid(String uid) throws Exception {
        return (String) XposedHelpers.callMethod(FriendsInfoServiceImpl, "getUinFromUid", uid);
    }

    public static void sendZan(String uin, int num) throws Exception {
        byte[] requestData = createZanRequestData(uin);
        int zanType = determineZanType(uin);

        XposedBridge.invokeOriginalMethod(sSendZan,
                ClassUtils.makeDefaultObject(sCardHandler, QQCurrentEnv.getQQAppInterface()),
                new Object[]{
                        Long.parseLong(QQCurrentEnv.getCurrentUin()),
                        Long.parseLong(uin),
                        requestData,
                        zanType,
                        num,
                        0
                });
    }

    private static byte[] createZanRequestData(String uin) throws Exception {
        byte[] data = new byte[10];
        data[0] = (byte) 12;
        data[1] = (byte) 24;
        data[2] = (byte) 0;
        data[3] = (byte) 1;
        data[4] = (byte) 6;
        data[5] = (byte) 1;
        data[6] = (byte) 49;
        data[7] = (byte) 22;
        data[8] = (byte) 1;

        if (FriendTool.isFriend(uin)) {
            data[9] = (byte) 49; // 好友类型
        } else {
            data[9] = (byte) 53; // 非好友类型
        }

        return data;
    }

    private static int determineZanType(String uin) throws Exception {
        return FriendTool.isFriend(uin) ? 1 : 5;
    }


}
