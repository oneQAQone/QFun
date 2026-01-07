package com.tencent.qqnt.kernel.nativeinterface;

public final class VASMsgAvatarPendant {
    public Integer avatarId;
    public Integer pendantDiyInfoId;
    public Long pendantId;

    public VASMsgAvatarPendant() {
    }

    public Integer getAvatarId() {
        return this.avatarId;
    }

    public Integer getPendantDiyInfoId() {
        return this.pendantDiyInfoId;
    }

    public Long getPendantId() {
        return this.pendantId;
    }

    public VASMsgAvatarPendant(Integer num, Long l, Integer num2) {
        this.avatarId = num;
        this.pendantId = l;
        this.pendantDiyInfoId = num2;
    }
}
