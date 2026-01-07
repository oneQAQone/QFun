package com.tencent.qqnt.kernel.nativeinterface;

public final class StructMsgElement {
    public String xmlContent;

    public StructMsgElement() {
    }

    public String getXmlContent() {
        return this.xmlContent;
    }

    public StructMsgElement(String str) {
        this.xmlContent = str;
    }
}
