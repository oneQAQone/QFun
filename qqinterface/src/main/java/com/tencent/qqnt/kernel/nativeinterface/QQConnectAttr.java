package com.tencent.qqnt.kernel.nativeinterface;

public final class QQConnectAttr {
    public long appID;
    public int appType;

    public QQConnectAttr() {
    }

    public long getAppID() {
        return this.appID;
    }

    public int getAppType() {
        return this.appType;
    }

    public QQConnectAttr(long j, int i) {
        this.appID = j;
        this.appType = i;
    }
}
