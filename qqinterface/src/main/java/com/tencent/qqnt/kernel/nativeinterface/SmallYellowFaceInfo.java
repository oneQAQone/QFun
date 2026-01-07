package com.tencent.qqnt.kernel.nativeinterface;



public final class SmallYellowFaceInfo {
    public String buf;
    public String compatibleText;
    public int index;
    public String text;

    public SmallYellowFaceInfo() {
    }

    public String getBuf() {
        return this.buf;
    }

    public String getCompatibleText() {
        return this.compatibleText;
    }

    public int getIndex() {
        return this.index;
    }

    public String getText() {
        return this.text;
    }

    public void setBuf(String str) {
        this.buf = str;
    }

    public void setCompatibleText(String str) {
        this.compatibleText = str;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public void setText(String str) {
        this.text = str;
    }

    public SmallYellowFaceInfo(int i, String str, String str2, String str3) {
        this.index = i;
        this.text = str;
        this.compatibleText = str2;
        this.buf = str3;
    }
}
