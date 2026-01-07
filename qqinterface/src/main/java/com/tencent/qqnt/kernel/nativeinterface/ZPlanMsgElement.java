package com.tencent.qqnt.kernel.nativeinterface;

public final class ZPlanMsgElement {
    public int actionFlag;
    public byte[] extInfo;
    public int guestActionID;
    public int masterActionID;

    public ZPlanMsgElement() {
        this.extInfo = new byte[0];
    }

    public int getActionFlag() {
        return this.actionFlag;
    }

    public byte[] getExtInfo() {
        return this.extInfo;
    }

    public int getGuestActionID() {
        return this.guestActionID;
    }

    public int getMasterActionID() {
        return this.masterActionID;
    }

    public ZPlanMsgElement(int i, int i2, int i3, byte[] bArr) {
        this.masterActionID = i;
        this.guestActionID = i2;
        this.actionFlag = i3;
        this.extInfo = bArr;
    }
}
