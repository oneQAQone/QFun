package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class Contact implements Serializable {
    public int chatType;
    public String guildId;
    public String peerUid;
    long serialVersionUID;

    public Contact() {
        this.serialVersionUID = 1L;
        this.peerUid = "";
        this.guildId = "";
    }

    public int getChatType() {
        return this.chatType;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getPeerUid() {
        return this.peerUid;
    }

    public void setChatType(int i) {
        this.chatType = i;
    }

    public void setGuildId(String str) {
        this.guildId = str;
    }

    public void setPeerUid(String str) {
        this.peerUid = str;
    }

    public Contact(int i, String str, String str2) {
        this.serialVersionUID = 1L;
        this.chatType = i;
        this.peerUid = str;
        this.guildId = str2;
    }
}
