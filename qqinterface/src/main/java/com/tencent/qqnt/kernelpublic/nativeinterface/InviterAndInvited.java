package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class InviterAndInvited implements Serializable {
    public GrayTipMember invited;
    public GrayTipMember inviter;
    long serialVersionUID;

    public InviterAndInvited() {
        this.serialVersionUID = 1L;
        this.inviter = new GrayTipMember();
        this.invited = new GrayTipMember();
    }

    public GrayTipMember getInvited() {
        return this.invited;
    }

    public GrayTipMember getInviter() {
        return this.inviter;
    }

    public InviterAndInvited(GrayTipMember grayTipMember, GrayTipMember grayTipMember2) {
        this.serialVersionUID = 1L;
        this.inviter = new GrayTipMember();
        new GrayTipMember();
        this.inviter = grayTipMember;
        this.invited = grayTipMember2;
    }
}
