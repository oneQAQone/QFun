package com.tencent.qqnt.kernel.nativeinterface;

public final class GiphyElement {
    public int height;
    public String id;
    public boolean isClip;
    public int width;

    public GiphyElement() {
        this.id = "";
    }

    public int getHeight() {
        return this.height;
    }

    public String getId() {
        return this.id;
    }

    public boolean getIsClip() {
        return this.isClip;
    }

    public int getWidth() {
        return this.width;
    }

    public GiphyElement(String str, boolean z, int i, int i2) {
        this.id = str;
        this.isClip = z;
        this.width = i;
        this.height = i2;
    }
}