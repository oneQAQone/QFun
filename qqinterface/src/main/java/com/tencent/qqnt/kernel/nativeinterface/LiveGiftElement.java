package com.tencent.qqnt.kernel.nativeinterface;

public final class LiveGiftElement {
    public int effectLevel;
    public String kStrGiftName;
    public String kStrReceiverTinyId;
    public long kUInt64GiftId;
    public long kUInt64GiftNum;
    public int materialId;
    public LiveGiftMemberInfo receiverMemberInfo;
    public int sendType;
    public LiveGiftMemberInfo senderMemberInfo;
    public String senderTinyId;

    public LiveGiftElement() {
        this.kStrReceiverTinyId = "";
        this.kStrGiftName = "";
        this.senderTinyId = "";
        this.senderMemberInfo = new LiveGiftMemberInfo();
        this.receiverMemberInfo = new LiveGiftMemberInfo();
    }

    public int getEffectLevel() {
        return this.effectLevel;
    }

    public String getKStrGiftName() {
        return this.kStrGiftName;
    }

    public String getKStrReceiverTinyId() {
        return this.kStrReceiverTinyId;
    }

    public long getKUInt64GiftId() {
        return this.kUInt64GiftId;
    }

    public long getKUInt64GiftNum() {
        return this.kUInt64GiftNum;
    }

    public int getMaterialId() {
        return this.materialId;
    }

    public LiveGiftMemberInfo getReceiverMemberInfo() {
        return this.receiverMemberInfo;
    }

    public int getSendType() {
        return this.sendType;
    }

    public LiveGiftMemberInfo getSenderMemberInfo() {
        return this.senderMemberInfo;
    }

    public String getSenderTinyId() {
        return this.senderTinyId;
    }

    public LiveGiftElement(String str, long j, long j2, String str2, int i, int i2, int i3, String str3, LiveGiftMemberInfo liveGiftMemberInfo, LiveGiftMemberInfo liveGiftMemberInfo2) {
        this.kStrReceiverTinyId = "";
        this.kStrGiftName = "";
        this.senderTinyId = "";
        this.senderMemberInfo = new LiveGiftMemberInfo();
        new LiveGiftMemberInfo();
        this.kStrReceiverTinyId = str;
        this.kUInt64GiftNum = j;
        this.kUInt64GiftId = j2;
        this.kStrGiftName = str2;
        this.materialId = i;
        this.effectLevel = i2;
        this.sendType = i3;
        this.senderTinyId = str3;
        this.senderMemberInfo = liveGiftMemberInfo;
        this.receiverMemberInfo = liveGiftMemberInfo2;
    }
}
