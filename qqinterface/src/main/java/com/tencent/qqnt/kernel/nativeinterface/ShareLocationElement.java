package com.tencent.qqnt.kernel.nativeinterface;

public final class ShareLocationElement {
    public String ext;
    public String text;

    public ShareLocationElement() {
    }

    public String getExt() {
        return this.ext;
    }

    public String getText() {
        return this.text;
    }

    public void setExt(String str) {
        this.ext = str;
    }

    public void setText(String str) {
        this.text = str;
    }

    public ShareLocationElement(String str, String str2) {
        this.text = str;
        this.ext = str2;
    }
}
