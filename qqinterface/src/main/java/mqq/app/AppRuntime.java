package mqq.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.tencent.qphone.base.remote.SimpleAccount;

import org.jetbrains.annotations.Nullable;

import mqq.app.api.IRuntimeService;
import mqq.manager.Manager;

public abstract class AppRuntime {
    public static final int ACCOUNT_MANAGER = 0;
    public static final int END_UN_LOGIN_MANAGER = 4;
    private static final int IMPORT_RUNTIME_DEPEND_CNT = 2;
    public static final int LOGIN_AUTO = 2;
    public static final int LOGIN_MANUAL = 1;
    public static final int LOGIN_UNINIT = 0;
    public static final String PROCESS = "process";
    public static final int PUSH_MANAGER = 4;
    private static final int RUNTIME_SERVICE_CAPACITY = 1024;
    public static final int SERVER_CONFIG_MANAGER = 3;
    public static final String SP_UIN_TO_UID = "MSF_SP_UIN_TO_UID";
    protected static final String TAG = "mqq";
    public static final int TICKET_MANAGER = 2;
    public static final int TYPE_CREATENEWRUNTIME_CHANGUIN_LOGIN = 4;
    public static final int TYPE_CREATENEWRUNTIME_DIRECT_LOGIN = 1;
    public static final int TYPE_CREATENEWRUNTIME_DIRECT_NET_LOGIN = 3;
    public static final int TYPE_CREATENEWRUNTIME_DOINIT = 5;
    public static final int TYPE_CREATENEWRUNTIME_SWITCHACCOUNT = 2;
    public static final int WTLOGIN_MANAGER = 1;

    public enum Status {
        online(11),
        offline(21),
        away(31),
        invisiable(41),
        busy(50),
        qme(60),
        dnd(70),
        receiveofflinemsg(95);

        private final int value;

        Status(int i) {
            this.value = i;
        }

        public static Status build(int i) {
            if (i != 11) {
                if (i != 21) {
                    if (i != 31) {
                        if (i != 41) {
                            if (i != 50) {
                                if (i != 60) {
                                    if (i != 70) {
                                        if (i != 95) {
                                            return null;
                                        }
                                        return receiveofflinemsg;
                                    }
                                    return dnd;
                                }
                                return qme;
                            }
                            return busy;
                        }
                        return invisiable;
                    }
                    return away;
                }
                return offline;
            }
            return online;
        }

        public int getValue() {
            return this.value;
        }
    }

    public void startServlet(NewIntent newIntent) {}

    @Deprecated
    public static boolean canAutoLoginFromCache(Context context, String str) {
        return true;
    }

    @Nullable
    private String getCurrentUidFromSp() {
        return null;
    }

    protected boolean canAutoLogin(String str) {
        return true;
    }

    public AppRuntime getAppRuntime(String str) {
        return null;
    }

    public MobileQQ getApplication() {
        return MobileQQ.getMobileQQ();
    }

    public Context getApplicationContext() {
        throw new RuntimeException("Stub!");
    }

    public Manager getManager(int i) {
        throw new RuntimeException("Stub!");
    }

    public final SharedPreferences getPreferences() {
        throw new RuntimeException("Stub!");
    }

    public byte[] getUinSign() {
        throw new RuntimeException("Stub!");
    }

    public void logout(boolean z) {
        logout(Constants.LogoutReason.user, z);
    }

    public void ntTriggerLogout(Constants.LogoutReason logoutReason) {
        logout(logoutReason, true);
    }

    protected void logout(Constants.LogoutReason logoutReason, boolean z) {
        throw new RuntimeException("Stub!");
    }

    public <T extends IRuntimeService> T getRuntimeService(Class<T> cls, String namespace) {
        throw new UnsupportedOperationException();
    }

    public <T extends IRuntimeService> T getRuntimeServiceIPCSync(@NonNull Class<T> cls, String str) {
        throw new UnsupportedOperationException();
    }

    public void switchAccount(SimpleAccount simpleAccount, String process) {

    }
    public String getAccount() {
        return "";
    }

    public abstract String getCurrentAccountUin();

    public String getCurrentUin() {
        return "";
    }

    public String getCurrentUid() {
        return "";
    }

    public long getLongAccountUin() {
        return 0;
    }

    public boolean isLogin() {
        return false;
    }
}
