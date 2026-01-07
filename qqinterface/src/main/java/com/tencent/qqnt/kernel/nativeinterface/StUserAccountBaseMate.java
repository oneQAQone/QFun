package com.tencent.qqnt.kernel.nativeinterface;

public final class StUserAccountBaseMate {
    public String uid;
    public long uin;

    public StUserAccountBaseMate() {
        this.uid = "";
    }

    public String getUid() {
        return this.uid;
    }

    public long getUin() {
        return this.uin;
    }

    public StUserAccountBaseMate(String str, long j) {
        this.uid = str;
        this.uin = j;
    }
}
