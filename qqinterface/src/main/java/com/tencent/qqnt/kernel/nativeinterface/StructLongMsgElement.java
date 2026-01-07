package com.tencent.qqnt.kernel.nativeinterface;

public final class StructLongMsgElement {
    public String resId;
    public String xmlContent;

    public StructLongMsgElement() {
    }

    public String getResId() {
        return this.resId;
    }

    public String getXmlContent() {
        return this.xmlContent;
    }

    public StructLongMsgElement(String str, String str2) {
        this.xmlContent = str;
        this.resId = str2;
    }
}
