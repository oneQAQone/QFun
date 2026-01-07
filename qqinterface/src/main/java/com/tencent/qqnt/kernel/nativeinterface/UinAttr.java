package com.tencent.qqnt.kernel.nativeinterface;

public final class UinAttr {
    public long addTime;
    public int familyRole;
    public long joinTime;
    public int priv;
    public int status;
    public int total;
    public String nick = "";
    public String shareAlbumid = "";
    public String qa = "";

    public long getAddTime() {
        return this.addTime;
    }

    public int getFamilyRole() {
        return this.familyRole;
    }

    public long getJoinTime() {
        return this.joinTime;
    }

    public String getNick() {
        return this.nick;
    }

    public int getPriv() {
        return this.priv;
    }

    public String getQa() {
        return this.qa;
    }

    public String getShareAlbumid() {
        return this.shareAlbumid;
    }

    public int getStatus() {
        return this.status;
    }

    public int getTotal() {
        return this.total;
    }
}
