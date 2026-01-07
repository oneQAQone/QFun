package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class LoverAlbumMeta {
    public int loveValue;
    public String fallInLoveTime = "";
    public ArrayList<TimeEventOp> loverEvents = new ArrayList<>();

    public String getFallInLoveTime() {
        return this.fallInLoveTime;
    }

    public int getLoveValue() {
        return this.loveValue;
    }

    public ArrayList<TimeEventOp> getLoverEvents() {
        return this.loverEvents;
    }
}
