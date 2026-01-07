package com.tencent.mobileqq.msf.sdk;

public class MSFSharedPreUtils {

    public static final String KEY_MSF_GUID = "msf_guid";
    private static final String KEY_REPORT_LOGIN_TIME_MILLIS_TODAY_ZERO = "key_report_login_time_millis_today_zero_";
    private static final String SP_REPORT_LOGIN_STATUS = "sp_report_login";
    public static final String TAG = "MSFSharedPreUtils";

    public static String getGuid() {
        throw new RuntimeException("Stub!");
    }

    /**
     * 获取指定账号在当天首次登录的绝对时间戳（毫秒）
     * @param uin QQ号
     * @return 绝对时间戳（毫秒）
     */
    public static long getLoginTimeMillisInTodayZero(String uin) {
        throw new RuntimeException("Stub!");
    }

    /**
     * 记录某个账号在当天的登录时间戳
     * @param uin QQ号
     * @param time 绝对时间戳（毫秒）
     */
    public static void setLoginTimeMillisInTodayZero(String uin, long time) {
        throw new RuntimeException("Stub!");
    }
}
