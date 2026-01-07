package mqq.app;

public interface Constants {
    public static final String FILE_INSTANCE_STATE = "savedInstanceState";
    public static final String KEY_SEQ = "seq";
    public static final String KEY_TICKET = "ticket";
    public static final String OPEN_SDK = "com.tencent.mobileqq:openSdk";

    interface Action {
        public static final int ACTION_ACTIVE_LOG_REPORT = 2219;
        public static final int ACTION_AFTER_FETCH_CONFIG_LOG_REPORT = 2223;
        public static final int ACTION_APP_GUARD = 2200;
        public static final int ACTION_CHANGE_TOKEN = 1032;
        public static final int ACTION_DELETE_ACCOUNT = 1007;
        public static final int ACTION_GET_ALTER_TICKETS = 2124;
        public static final int ACTION_GET_KEY = 1030;
        public static final int ACTION_GET_PLUGIN_CONFIG = 1018;
        public static final int ACTION_GET_RICH_MEDIA_SERVER_LIST = 1011;
        public static final int ACTION_GET_TICKET_NO_PASSWD = 1101;
        public static final int ACTION_LOGIN = 1001;
        public static final int ACTION_LOGIN_START_NOTIFY = 2216;
        public static final int ACTION_MANUAL_SET_LOG_LEVEL = 2210;
        public static final int ACTION_MSF_UPDATE_LOCALE_ID = 2212;
        public static final int ACTION_NET_EXCEPTION_EVENT = 1042;
        public static final int ACTION_NOTIFY_MSF_MSG_SYNC_END = 2226;
        public static final int ACTION_NOTIFY_MSF_ONLINE_REGISTER = 2228;
        public static final int ACTION_NOTIFY_MSF_WEAK_NET_SWITCH = 2222;
        public static final int ACTION_NOTIFY_REFRESH_WEBVIEW_TICKET = 2123;
        public static final int ACTION_ONLINE_STATUS_PUSH = 2211;
        public static final int ACTION_OPEN_PCACTIVE = 2128;
        public static final int ACTION_REGISTNEWACCOUNT_REFETCH_SMS = 1020;
        public static final int ACTION_REGIST_COMMAND_PUSH = 1040;
        public static final int ACTION_REGIST_MESSAGE_PUSH = 1002;
        public static final int ACTION_REGIST_MESSAGE_PUSH_PROXY = 1025;
        public static final int ACTION_REMOVE_MSF_CONFIG = 2229;
        public static final int ACTION_REPORT = 1012;
        public static final int ACTION_SAVE_GEOG_INFO = 2204;
        public static final int ACTION_SECURITY_SDK = 2218;
        public static final int ACTION_SEND_MSGSIGNAL = 2202;
        public static final int ACTION_SEND_WIRELESS_MEIBAOREQ = 1044;
        public static final int ACTION_SEND_WIRELESS_PSWREQ = 1043;
        public static final int ACTION_SEND_WTPKG = 2203;
        public static final int ACTION_SSO_GET_A1_WITH_A1 = 1102;
        public static final int ACTION_SSO_LOGIN_ACCOUNT = 1100;
        public static final int ACTION_START_PCACTIVE_POLLING = 2126;
        public static final int ACTION_STOP_PCACTIVE_POLLING = 2127;
        public static final int ACTION_SUBACCOUNT_GETKEY = 1037;
        public static final int ACTION_SUBACCOUNT_LOGIN = 1035;
        public static final int ACTION_TRANSFILE_CHECK_MSF_CONERRO = 2201;
        public static final int ACTION_UNREGIST_MESSAGE_PUSH_PROXY = 1026;
        public static final int ACTION_UNREGIST_PROXY = 1028;
        public static final int ACTION_UPDATE_BEFORE_MSG_SYNC_CONTROL_CONFIG = 2227;
        public static final int ACTION_UPDATE_COMPLEX_CONNECT_CONFIG = 2224;
        public static final int ACTION_UPDATE_MSF_COMMON_CONFIG = 2221;
        public static final int ACTION_UPDATE_MSF_CONFIG = 2214;
        public static final int ACTION_UPDATE_MSF_MMKV_CONFIG = 2225;
        public static final int ACTION_UPDATE_MSF_TOGGLE = 2217;
        public static final int ACTION_UPDATE_MSF_WEAK_NET_CONFIG = 2220;
        public static final int ACTION_VERIFY_PASSWD = 2205;
        public static final int ACTION_VERIFY_PASSWD_REFRESH_IMAGECODE = 2207;
        public static final int ACTION_VERIFY_PASSWD_SUBMIT_IMAGECODE = 2206;
        public static final int ACTION_VERITYCODE_CLOSE = 1024;
        public static final int ACTION_VERITYCODE_RECV = 1023;
        public static final int ACTION_WTLOGIN_ASK_DEV_LOCK_SMS = 2109;
        public static final int ACTION_WTLOGIN_CANCEL_CODE = 2213;
        public static final int ACTION_WTLOGIN_CHECK_DEV_LOCK_SMS = 2110;
        public static final int ACTION_WTLOGIN_CHECK_DEV_LOCK_STATUS = 2108;
        public static final int ACTION_WTLOGIN_CHECK_PICTURE_AND_GET_ST = 2102;
        public static final int ACTION_WTLOGIN_CHECK_SMS_AND_GET_ST = 2113;
        public static final int ACTION_WTLOGIN_CHECK_SMS_AND_GET_ST_EXT = 2114;
        public static final int ACTION_WTLOGIN_CHECK_SMS_VERIFY_LOGIN_ACCOUNT = 2118;
        public static final int ACTION_WTLOGIN_CLOSE_CODE = 2105;
        public static final int ACTION_WTLOGIN_CLOSE_DEV_LOCK = 2111;
        public static final int ACTION_WTLOGIN_EXCEPTION = 2107;
        public static final int ACTION_WTLOGIN_GET_A1_WITH_A1 = 2106;
        public static final int ACTION_WTLOGIN_GET_OPEN_KEY_WITHOUT_PASSWD = 2208;
        public static final int ACTION_WTLOGIN_GET_ST_VIA_SMS_VERIFY_LOGIN = 2121;
        public static final int ACTION_WTLOGIN_GET_ST_WITHOUT_PASSWD = 2101;
        public static final int ACTION_WTLOGIN_GET_ST_WITH_PASSWD = 2100;
        public static final int ACTION_WTLOGIN_GET_SUBACCOUNT_ST_VIA_SMS_VERIFY_LOGIN = 2122;
        public static final int ACTION_WTLOGIN_QUICK_LOGIN_BY_GATEWAY = 2215;
        public static final int ACTION_WTLOGIN_REFRESH_PICTURE_DATA = 2103;
        public static final int ACTION_WTLOGIN_REFRESH_SMS_DATA = 2112;
        public static final int ACTION_WTLOGIN_REFRESH_SMS_VERIFY_LOGIN_CODE = 2119;
        public static final int ACTION_WTLOGIN_REG_GET_SMS_VERIFY_LOGIN_ACCOUNT = 2117;
        public static final int ACTION_WTLOGIN_SET_DEVLOCK_MOBILE_TYPE = 2129;
        public static final int ACTION_WTLOGIN_SET_REG_DEV_LOCK_FLAG = 2125;
        public static final int ACTION_WTLOGIN_VERIFY_CODE = 2104;
        public static final int ACTION_WTLOGIN_VERIFY_SMS_VERIFY_LOGIN_CODE = 2120;
    }

    enum Key {
        nickname,
        _logintime,
        SvcRegister_timeStamp,
        currentAccount
    }

    enum LogoutReason {
        user,
        expired,
        gray,
        kicked,
        tips,
        suspend,
        forceLogout,
        switchAccount,
        secKicked,
        restartProcess
    }

    enum PropertiesKey {
        uinDisplayName,
        nickName;

        public String toString() {
            return name();
        }
    }
}
