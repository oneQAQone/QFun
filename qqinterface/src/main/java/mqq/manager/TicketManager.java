package mqq.manager;

import android.content.Context;

import java.util.HashMap;

import mqq.app.TicketManagerListener;
import oicq.wlogin_sdk.request.Ticket;
import oicq.wlogin_sdk.request.WTLoginRecordSnapshot;
import oicq.wlogin_sdk.request.WtTicketPromise;

public interface TicketManager extends Manager {
    int addWTLoginRecordFromNT(WTLoginRecordSnapshot wTLoginRecordSnapshot);

    void clearA1(long j, int i);

    byte[] getA1(long j, long j2, byte[] bArr);

    String getA2(String str);

    void getA2(long j, int i, MainTicketCallback mainTicketCallback);

    void getD2(long j, int i, MainTicketCallback mainTicketCallback);

    Ticket getD2Ticket(String str);

    void getMainTicket(long j, int i, MainTicketCallback mainTicketCallback);

    String getOpenA2(String str);

    String getPskey(String str, String str2);

    Ticket getPskey(String str, long j, String[] strArr, WtTicketPromise wtTicketPromise);

    Ticket getPskeyForOpen(String str, long j, String[] strArr, byte[] bArr, WtTicketPromise wtTicketPromise);

    String getPt4Token(String str, String str2);

    String getRealSkey(String str);

    @Deprecated
    String getSkey(String str);

    byte[] getSt(String str, int i);

    byte[] getStkey(String str, int i);

    String getStweb(String str);

    String getSuperkey(String str);

    WTLoginRecordSnapshot getWTLoginRecordSnapshot(long j, int i);

    boolean isQQNTSignToWTRefreshSuccess(long j);

    void registTicketManagerListener(TicketManagerListener ticketManagerListener);

    void reloadCache(Context context);

    int sendRPCData(long j, String str, String str2, byte[] bArr, int i);

    void setAlterTicket(HashMap<String, String> hashMap);

    void unregistTicketManagerListener(TicketManagerListener ticketManagerListener);

    boolean useAsyncTicketInterface();
}
