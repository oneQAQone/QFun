package mqq.manager;

public interface MainTicketCallback {
    void onFail(int i, String str);

    void onSuccess(MainTicketInfo mainTicketInfo);
}
