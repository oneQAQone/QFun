package com.tencent.qqnt.kernel.nativeinterface;

public final class AVGroupAudioMemberModel {
    public int AVState;
    public int invitedTimestamp;
    public boolean isInvited;
    public boolean isMicOff;
    public boolean isMicOffBySelf;
    public int terminalType;
    public String uid;

    public AVGroupAudioMemberModel() {
        this.uid = "";
    }

    public int getAVState() {
        return this.AVState;
    }

    public int getInvitedTimestamp() {
        return this.invitedTimestamp;
    }

    public boolean getIsInvited() {
        return this.isInvited;
    }

    public boolean getIsMicOff() {
        return this.isMicOff;
    }

    public boolean getIsMicOffBySelf() {
        return this.isMicOffBySelf;
    }

    public int getTerminalType() {
        return this.terminalType;
    }

    public String getUid() {
        return this.uid;
    }

    public AVGroupAudioMemberModel(boolean z, int i, String str, int i2, int i3, boolean z2, boolean z3) {
        this.isInvited = z;
        this.invitedTimestamp = i;
        this.uid = str;
        this.AVState = i2;
        this.terminalType = i3;
        this.isMicOff = z2;
        this.isMicOffBySelf = z3;
    }
}
