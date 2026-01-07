package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class NewBuddyGrayElement implements Serializable {
    public String friendNick;
    public boolean isInitiator;
    long serialVersionUID;
    public int sourceId;
    public int subSourceId;
    public long time;

    public NewBuddyGrayElement() {
        this.serialVersionUID = 1L;
        this.friendNick = "";
    }

    public String getFriendNick() {
        return this.friendNick;
    }

    public boolean getIsInitiator() {
        return this.isInitiator;
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getSubSourceId() {
        return this.subSourceId;
    }

    public long getTime() {
        return this.time;
    }

    public NewBuddyGrayElement(long j, int i, int i2, boolean z, String str) {
        this.serialVersionUID = 1L;
        this.time = j;
        this.sourceId = i;
        this.subSourceId = i2;
        this.isInitiator = z;
        this.friendNick = str;
    }
}
