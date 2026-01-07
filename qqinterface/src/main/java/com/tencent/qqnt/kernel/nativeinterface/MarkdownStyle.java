package com.tencent.qqnt.kernel.nativeinterface;

public final class MarkdownStyle {
    public int bubbleType;
    public String mainFontSize = "";
    public String layOut = "";

    public int getBubbleType() {
        return this.bubbleType;
    }

    public String getLayOut() {
        return this.layOut;
    }

    public String getMainFontSize() {
        return this.mainFontSize;
    }
}
