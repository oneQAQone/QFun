package com.tencent.qqnt.kernel.nativeinterface;

public final class ArkElement {
    public String bytesData;
    public LinkInfo linkInfo;
    public Integer subElementType;

    public ArkElement() {
        this.bytesData = "";
    }

    public String getBytesData() {
        return this.bytesData;
    }

    public LinkInfo getLinkInfo() {
        return this.linkInfo;
    }

    public Integer getSubElementType() {
        return this.subElementType;
    }

    public ArkElement(String str, LinkInfo linkInfo, Integer num) {
        this.bytesData = str;
        this.linkInfo = linkInfo;
        this.subElementType = num;
    }
}