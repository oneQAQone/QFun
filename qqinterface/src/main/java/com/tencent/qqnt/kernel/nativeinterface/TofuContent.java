package com.tencent.qqnt.kernel.nativeinterface;

public final class TofuContent {
    public String color;
    public String title;

    public TofuContent() {
    }

    public String getColor() {
        return this.color;
    }

    public String getTitle() {
        return this.title;
    }

    public TofuContent(String str, String str2) {
        this.title = str;
        this.color = str2;
    }
}
