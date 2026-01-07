package com.tencent.qqnt.kernel.nativeinterface;

public final class StPicUrl {
    public int height;
    public String url;
    public int width;

    public StPicUrl() {
        this.url = "";
    }

    public int getHeight() {
        return this.height;
    }

    public String getUrl() {
        return this.url;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public StPicUrl(String str, int i, int i2) {
        this.url = str;
        this.width = i;
        this.height = i2;
    }
}
