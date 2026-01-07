package com.tencent.qqnt.kernel.nativeinterface;

public final class InviteInfo {
    public String fromUid;
    public int inviteType;
    public long relationId;

    public InviteInfo() {
        this.fromUid = "";
    }

    public String getFromUid() {
        return this.fromUid;
    }

    public int getInviteType() {
        return this.inviteType;
    }

    public long getRelationId() {
        return this.relationId;
    }

    public InviteInfo(long j, int i, String str) {
        this.relationId = j;
        this.inviteType = i;
        this.fromUid = str;
    }
}
