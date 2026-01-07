package com.tencent.qqnt.kernel.nativeinterface;

public final class MultiForwardMsgElement {
    public String fileName;
    public String resId;
    public String xmlContent;

    public MultiForwardMsgElement() {
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getResId() {
        return this.resId;
    }

    public String getXmlContent() {
        return this.xmlContent;
    }

    public MultiForwardMsgElement(String str, String str2, String str3) {
        this.xmlContent = str;
        this.resId = str2;
        this.fileName = str3;
    }
}
