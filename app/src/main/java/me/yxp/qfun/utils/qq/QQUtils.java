package me.yxp.qfun.utils.qq;

import android.app.Activity;
import android.widget.Toast;

import java.lang.reflect.Method;

import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class QQUtils {
    private static Class<?> sCardHandler;
    private static Class<?> sRelationNTUinAndUidApiImpl;

    private static Method sShowQQToastInUiThread;
    private static Method sSendZan;
    private static Method sGetUidFromUin;
    private static Method sGetUinFromUid;
    private static Method sIsFriend;
    private static Method sGetService;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("QQUtils", th);
        }
    }

    private static void initMethod() throws Throwable {
        Class<?> qqToastUtil = ClassUtils.load("com.tencent.util.QQToastUtil");
        sCardHandler = ClassUtils._CardHandler();
        sRelationNTUinAndUidApiImpl = ClassUtils.load("com.tencent.relation.common.api.impl.RelationNTUinAndUidApiImpl");
        Class<?> friendDataServiceImpl = ClassUtils.load("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl");

        initializeMethods(qqToastUtil, friendDataServiceImpl);
    }

    private static void initializeMethods(Class<?> qqToastUtil, Class<?> friendDataServiceImpl) throws Exception {
        sShowQQToastInUiThread = MethodUtils.create(qqToastUtil)
                .withMethodName("showQQToastInUiThread")
                .findOne();

        sSendZan = MethodUtils.create(sCardHandler)
                .withReturnType(void.class)
                .withParamTypes(long.class, long.class, byte[].class, int.class, int.class, int.class)
                .findOne();

        sGetUidFromUin = MethodUtils.create(sRelationNTUinAndUidApiImpl)
                .withMethodName("getUidFromUin")
                .withParamTypes(String.class)
                .findOne();

        sGetUinFromUid = MethodUtils.create(sRelationNTUinAndUidApiImpl)
                .withMethodName("getUinFromUid")
                .withParamTypes(String.class)
                .findOne();

        sIsFriend = MethodUtils.create(friendDataServiceImpl)
                .withMethodName("isFriend")
                .findOne();

        sGetService = MethodUtils.create(friendDataServiceImpl)
                .withMethodName("getService")
                .findOne();
    }

    public static void Toast(String message) {
        SyncUtils.runOnUiThread(() -> {
            Activity activity = QQCurrentEnv.getActivity();
            if (activity != null) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void QQToast(int icon, String message) {
        try {
            sShowQQToastInUiThread.invoke(null, icon, message);
        } catch (Exception ignored) {
            // 忽略异常，保持静默
        }
    }

    public static boolean isFriend(String uin) throws Exception {
        Object friendDataServiceImpl = sGetService.invoke(null, QQCurrentEnv.getQQAppInterface());
        return (boolean) sIsFriend.invoke(friendDataServiceImpl, uin);
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

        if (isFriend(uin)) {
            data[9] = (byte) 49; // 好友类型
        } else {
            data[9] = (byte) 53; // 非好友类型
        }

        return data;
    }

    private static int determineZanType(String uin) throws Exception {
        return isFriend(uin) ? 1 : 5;
    }

    public static String getUidFromUin(String uin) throws Exception {
        Object relationNTUinAndUidApiImpl = ClassUtils.makeDefaultObject(sRelationNTUinAndUidApiImpl);
        return sGetUidFromUin.invoke(relationNTUinAndUidApiImpl, uin).toString();
    }

    public static String getUinFromUid(String uid) throws Exception {
        Object relationNTUinAndUidApiImpl = ClassUtils.makeDefaultObject(sRelationNTUinAndUidApiImpl);
        return sGetUinFromUid.invoke(relationNTUinAndUidApiImpl, uid).toString();
    }
}
