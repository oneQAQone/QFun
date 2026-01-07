package com.tencent.qqnt.kernel.nativeinterface;

public final class YoloGameUserInfo {
    public String bizId;
    public int rank;
    public int result;
    public String uid;

    public YoloGameUserInfo() {
        this.uid = "";
        this.bizId = "";
    }

    public String getBizId() {
        return this.bizId;
    }

    public int getRank() {
        return this.rank;
    }

    public int getResult() {
        return this.result;
    }

    public String getUid() {
        return this.uid;
    }

    public YoloGameUserInfo(String str, int i, int i2, String str2) {
        this.uid = str;
        this.result = i;
        this.rank = i2;
        this.bizId = str2;
    }
}
