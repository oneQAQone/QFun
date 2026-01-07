package oicq.wlogin_sdk.tools;

public class InternationMsg {
    static a[] a = new a[]{new a(2052, MSG_TYPE.MSG_0, "登录失败"), new a(1028, MSG_TYPE.MSG_0, "登錄失敗"), new a(1033, MSG_TYPE.MSG_0, "Unable to login"), new a(2052, MSG_TYPE.MSG_1, "请你稍后重试。"), new a(1028, MSG_TYPE.MSG_1, "請你稍後重試。"), new a(1033, MSG_TYPE.MSG_1, "Please try again later."), new a(2052, MSG_TYPE.MSG_2, "手机存储异常，请删除帐号重试。"), new a(1028, MSG_TYPE.MSG_2, "手機存儲異常，請刪除帳號重試。"), new a(1033, MSG_TYPE.MSG_2, "Phone memory error, please delete the account and try again."), new a(2052, MSG_TYPE.MSG_3, "请求失败，请你稍后重试。"), new a(1028, MSG_TYPE.MSG_3, "請求失敗，請你稍後重試。"), new a(1033, MSG_TYPE.MSG_3, "Request failed, please try again later."), new a(2052, MSG_TYPE.MSG_4, "网络超时，请你稍后重试。"), new a(1028, MSG_TYPE.MSG_4, "網絡超時，請你稍後重試。"), new a(1033, MSG_TYPE.MSG_4, "Network timeout, please try again later."), new a(2052, MSG_TYPE.MSG_5, "登录设备保护"), new a(1028, MSG_TYPE.MSG_5, "登錄設備保護"), new a(1033, MSG_TYPE.MSG_5, "Device Protection")};

    public enum MSG_TYPE {
        MSG_0,
        MSG_1,
        MSG_2,
        MSG_3,
        MSG_4,
        MSG_5
    }

    static class a {
        int a;
        MSG_TYPE b;
        String c;

        public a(int i, MSG_TYPE msg_type, String str) {
            this.a = i;
            this.b = msg_type;
            this.c = str;
        }
    }

    public static String a(MSG_TYPE msg_type) {
        throw new RuntimeException("Stub!");
    }
}
