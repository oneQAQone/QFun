package com.tencent.qqnt.kernel.nativeinterface;

public final class StBanner {
    public int actionType;
    public long intervalTime;
    public String id = "";
    public String img = "";
    public String color = "";
    public String text = "";
    public String actionUrl = "";

    public int getActionType() {
        return this.actionType;
    }

    public String getActionUrl() {
        return this.actionUrl;
    }

    public String getColor() {
        return this.color;
    }

    public String getId() {
        return this.id;
    }

    public String getImg() {
        return this.img;
    }

    public long getIntervalTime() {
        return this.intervalTime;
    }

    public String getText() {
        return this.text;
    }
}
