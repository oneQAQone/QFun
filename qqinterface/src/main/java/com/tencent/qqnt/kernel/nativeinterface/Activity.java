package com.tencent.qqnt.kernel.nativeinterface;

public final class Activity {
    public int count;
    public Media recentMedia = new Media();
    public String text = "";

    public int getCount() {
        return this.count;
    }

    public Media getRecentMedia() {
        return this.recentMedia;
    }

    public String getText() {
        return this.text;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void setRecentMedia(Media media) {
        this.recentMedia = media;
    }

    public void setText(String str) {
        this.text = str;
    }
}
