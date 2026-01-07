package com.tencent.qqnt.kernel.nativeinterface;

public final class MarkdownElement {
    public String content;
    public MarkdownElementExtInfo mdExtInfo;
    public int mdExtType;
    public String mdSummary;
    public String processMsg;
    public MarkdownStyle style;

    public MarkdownElement() {
        this.content = "";
        this.processMsg = "";
        this.mdSummary = "";
    }

    public String getContent() {
        return this.content;
    }

    public MarkdownElementExtInfo getMdExtInfo() {
        return this.mdExtInfo;
    }

    public int getMdExtType() {
        return this.mdExtType;
    }

    public String getMdSummary() {
        return this.mdSummary;
    }

    public String getProcessMsg() {
        return this.processMsg;
    }

    public MarkdownStyle getStyle() {
        return this.style;
    }

    public MarkdownElement(String str, String str2, int i, MarkdownElementExtInfo markdownElementExtInfo) {
        this.processMsg = "";
        this.content = str;
        this.mdSummary = str2;
        this.mdExtType = i;
        this.mdExtInfo = markdownElementExtInfo;
    }
}
