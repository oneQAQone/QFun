package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class GrayTipGroupMember implements Serializable {
    public String card;
    public String name;
    public MemberRole role;
    long serialVersionUID;
    public String uid;

    public GrayTipGroupMember() {
        this.serialVersionUID = 1L;
        this.uid = "";
        this.card = "";
        this.name = "";
        this.role = MemberRole.values()[0];
    }

    public String getCard() {
        return this.card;
    }

    public String getName() {
        return this.name;
    }

    public MemberRole getRole() {
        return this.role;
    }

    public String getUid() {
        return this.uid;
    }

    public GrayTipGroupMember(String str, String str2, String str3, MemberRole memberRole) {
        this.serialVersionUID = 1L;
        this.uid = "";
        this.card = "";
        this.name = "";
        MemberRole memberRole2 = MemberRole.values()[0];
        this.uid = str;
        this.card = str2;
        this.name = str3;
        this.role = memberRole;
    }
}
