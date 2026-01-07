package mqq.app;

import android.content.Context;

import java.util.HashMap;

import mqq.manager.MainTicketCallback;
import mqq.manager.TicketManager;
import oicq.wlogin_sdk.request.Ticket;
import oicq.wlogin_sdk.request.WTLoginRecordSnapshot;
import oicq.wlogin_sdk.request.WtTicketPromise;

public class TicketManagerImpl implements TicketManager {
    public static final int APPID_QQ = 16;
    public static final int BUFLAG_OPEN_CONNECT = 0x5f5e1604;
    private static final String EVENT_CHANGE_TOKEN_SRC = "wt_change_token_src";
    private static final int FAIL_NOT_PSKEY_DOMAIN = -5;
    private static final int FAIL_SDK_EXCEPTION = -4;
    private static final int FAIL_TICKET_NULL = -2;
    private static final int FAIL_TICKET_SIG_NULL = -3;
    private static final int FAIL_UIN_NONE = -1;
    private static final String KEY_SRC = "src";
    private static final String TAG = "TicketManager";
    public static final int TEN_MINUTE = 600000;
    public static final String WTLOGIN_GET_A2_FAIL = "wtlogin_get_a2_fail";
    private final AppRuntime mApp;
    private HashMap<String, String> mAlterTicketsMap = new HashMap<>();

    public TicketManagerImpl(AppRuntime appRuntime) {
        throw new RuntimeException("Stub!");
    }

    private boolean useNewWt() {
        throw new RuntimeException("Stub!");
    }

    public int addWTLoginRecordFromNT(WTLoginRecordSnapshot wTLoginRecordSnapshot) {
        throw new RuntimeException("Stub!");
    }

    public void clearA1(long j, int i) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getA1(long j, long j2, byte[] bArr) {
        throw new RuntimeException("Stub!");
    }

    public String getA2(String str) {
        throw new RuntimeException("Stub!");
    }

    public void getA2(long j, int i, MainTicketCallback mainTicketCallback) {
        throw new RuntimeException("Stub!");
    }

    public void getD2(long j, int i, MainTicketCallback mainTicketCallback) {
        throw new RuntimeException("Stub!");
    }

    public Ticket getD2Ticket(String str) {
        throw new RuntimeException("Stub!");
    }

    public void getMainTicket(long j, int i, MainTicketCallback mainTicketCallback) {
        throw new RuntimeException("Stub!");
    }

    public String getOpenA2(String str) {
        throw new RuntimeException("Stub!");
    }

    public String getPskey(String str, String str2) {
        throw new RuntimeException("Stub!");
    }

    public Ticket getPskey(String str, long j, String[] strArr, WtTicketPromise wtTicketPromise) {
        throw new RuntimeException("Stub!");
    }

    public Ticket getPskeyForOpen(String str, long j, String[] strArr, byte[] bArr, WtTicketPromise wtTicketPromise) {
        throw new RuntimeException("Stub!");
    }

    public String getPt4Token(String str, String str2) {
        throw new RuntimeException("Stub!");
    }

    public String getRealSkey(String str) {
        throw new RuntimeException("Stub!");
    }

    public String getSkey(String str) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getSt(String str, int i) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getStkey(String str, int i) {
        throw new RuntimeException("Stub!");
    }

    public String getStweb(String str) {
        throw new RuntimeException("Stub!");
    }

    public String getSuperkey(String str) {
        throw new RuntimeException("Stub!");
    }

    public WTLoginRecordSnapshot getWTLoginRecordSnapshot(long j, int i) {
        throw new RuntimeException("Stub!");
    }

    public boolean isQQNTSignToWTRefreshSuccess(long j) {
        throw new RuntimeException("Stub!");
    }

    public void registTicketManagerListener(TicketManagerListener ticketManagerListener) {
        throw new RuntimeException("Stub!");
    }

    public void reloadCache(Context context) {
        throw new RuntimeException("Stub!");
    }

    public int sendRPCData(long j, String str, String str2, byte[] bArr, int i) {
        throw new RuntimeException("Stub!");
    }

    public void setAlterTicket(HashMap<String, String> hashMap) {
        if (hashMap != null && this.mApp != null) {
            String str = hashMap.get("uin");
            String str2 = hashMap.get("A2");
            String str3 = hashMap.get("vkey");
            String str4 = hashMap.get("skey");
            String str5 = hashMap.get("stweb");
            String str6 = hashMap.get("superkey");
            AppRuntime appRuntime = this.mApp;
            if (appRuntime != null && appRuntime.getAccount() != null && this.mApp.getAccount().equals(str)) {
                this.mAlterTicketsMap.put("A2", str2);
                this.mAlterTicketsMap.put("vkey", str3);
                this.mAlterTicketsMap.put("skey", str4);
                this.mAlterTicketsMap.put("stweb", str5);
                this.mAlterTicketsMap.put("superkey", str6);
            }
        }
    }

    public void unregistTicketManagerListener(TicketManagerListener ticketManagerListener) {
        throw new RuntimeException("Stub!");
    }

    public boolean useAsyncTicketInterface() {
        throw new RuntimeException("Stub!");
    }

    public void onDestroy() {
    }
}
