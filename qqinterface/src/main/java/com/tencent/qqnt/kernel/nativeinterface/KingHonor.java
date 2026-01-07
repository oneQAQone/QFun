package com.tencent.qqnt.kernel.nativeinterface;

public final class KingHonor {
    public Integer groupInfoFlagEx4;
    public byte[] groupMsgBusiBuf;
    public Integer kingHonorLevel;

    public KingHonor() {
    }

    public Integer getGroupInfoFlagEx4() {
        return this.groupInfoFlagEx4;
    }

    public byte[] getGroupMsgBusiBuf() {
        return this.groupMsgBusiBuf;
    }

    public Integer getKingHonorLevel() {
        return this.kingHonorLevel;
    }

    public KingHonor(Integer num, Integer num2, byte[] bArr) {
        this.kingHonorLevel = num;
        this.groupInfoFlagEx4 = num2;
        this.groupMsgBusiBuf = bArr;
    }
}
