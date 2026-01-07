package com.tencent.qqnt.kernel.nativeinterface;

public final class TaskTopMsgElement {
    public String iconUrl;
    public String msgSummary;
    public String msgTitle;
    public int topMsgType;

    public TaskTopMsgElement() {
        this.msgTitle = "";
        this.msgSummary = "";
        this.iconUrl = "";
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getMsgSummary() {
        return this.msgSummary;
    }

    public String getMsgTitle() {
        return this.msgTitle;
    }

    public int getTopMsgType() {
        return this.topMsgType;
    }

    public TaskTopMsgElement(String str, String str2, String str3, int i) {
        this.msgTitle = str;
        this.msgSummary = str2;
        this.iconUrl = str3;
        this.topMsgType = i;
    }
}
