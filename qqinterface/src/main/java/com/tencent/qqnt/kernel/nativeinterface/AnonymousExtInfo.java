package com.tencent.qqnt.kernel.nativeinterface;

public final class AnonymousExtInfo {
    public int anonymousFlag;
    public byte[] anonymousId;
    public String anonymousNick;
    public long bubbleId;
    public long expireTime;
    public long headPicIndex;
    public String rankColor;

    public AnonymousExtInfo() {
        this.anonymousId = new byte[0];
        this.anonymousNick = "";
        this.rankColor = "";
    }

    public int getAnonymousFlag() {
        return this.anonymousFlag;
    }

    public byte[] getAnonymousId() {
        return this.anonymousId;
    }

    public String getAnonymousNick() {
        return this.anonymousNick;
    }

    public long getBubbleId() {
        return this.bubbleId;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public long getHeadPicIndex() {
        return this.headPicIndex;
    }

    public String getRankColor() {
        return this.rankColor;
    }

    public AnonymousExtInfo(int i, byte[] bArr, String str, long j, long j2, long j3, String str2) {
        this.anonymousFlag = i;
        this.anonymousId = bArr;
        this.anonymousNick = str;
        this.headPicIndex = j;
        this.expireTime = j2;
        this.bubbleId = j3;
        this.rankColor = str2;
    }
}
