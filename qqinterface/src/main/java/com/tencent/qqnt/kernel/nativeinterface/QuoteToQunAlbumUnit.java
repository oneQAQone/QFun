package com.tencent.qqnt.kernel.nativeinterface;

public final class QuoteToQunAlbumUnit {
    public int duration;
    public int height;
    public long mediaSize;
    public int mediaType;
    public int storeId;
    public long uin;
    public int width;
    public String mediaId = "";
    public String mediaMd5 = "";
    public String dittoUin = "";
    public String sha1 = "";
    public String mediaName = "";

    public String getDittoUin() {
        return this.dittoUin;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getHeight() {
        return this.height;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public String getMediaMd5() {
        return this.mediaMd5;
    }

    public String getMediaName() {
        return this.mediaName;
    }

    public long getMediaSize() {
        return this.mediaSize;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public String getSha1() {
        return this.sha1;
    }

    public int getStoreId() {
        return this.storeId;
    }

    public long getUin() {
        return this.uin;
    }

    public int getWidth() {
        return this.width;
    }

    public void setDittoUin(String str) {
        this.dittoUin = str;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setMediaId(String str) {
        this.mediaId = str;
    }

    public void setMediaMd5(String str) {
        this.mediaMd5 = str;
    }

    public void setMediaName(String str) {
        this.mediaName = str;
    }

    public void setMediaSize(long j) {
        this.mediaSize = j;
    }

    public void setMediaType(int i) {
        this.mediaType = i;
    }

    public void setSha1(String str) {
        this.sha1 = str;
    }

    public void setStoreId(int i) {
        this.storeId = i;
    }

    public void setUin(long j) {
        this.uin = j;
    }

    public void setWidth(int i) {
        this.width = i;
    }
}
