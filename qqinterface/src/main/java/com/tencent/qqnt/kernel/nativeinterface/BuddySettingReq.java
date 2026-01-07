package com.tencent.qqnt.kernel.nativeinterface;

public final class BuddySettingReq {
    public byte[] name;
    public byte[] name1;
    public int sourceId;
    public int sourceSubId;
    public AccountInfo targetInfo = new AccountInfo();
    public int version;

    public byte[] getName() {
        return this.name;
    }

    public byte[] getName1() {
        return this.name1;
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getSourceSubId() {
        return this.sourceSubId;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }

    public int getVersion() {
        return this.version;
    }
}
