package com.tencent.qqnt.kernel.nativeinterface;

public final class LinkInfo {
    public String desc;
    public String icon;
    public boolean isDefaultPicture;
    public RichStatus richStatus;
    public Integer tencentDocType;
    public String title;

    public LinkInfo() {
        this.richStatus = RichStatus.values()[0];
    }

    public String getDesc() {
        return this.desc;
    }

    public String getIcon() {
        return this.icon;
    }

    public boolean getIsDefaultPicture() {
        return this.isDefaultPicture;
    }

    public RichStatus getRichStatus() {
        return this.richStatus;
    }

    public Integer getTencentDocType() {
        return this.tencentDocType;
    }

    public String getTitle() {
        return this.title;
    }

    public LinkInfo(String str, String str2, String str3, RichStatus richStatus, Integer num) {
        RichStatus richStatus2 = RichStatus.values()[0];
        this.title = str;
        this.icon = str2;
        this.desc = str3;
        this.richStatus = richStatus;
        this.tencentDocType = num;
    }
}