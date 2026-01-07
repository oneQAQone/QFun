package com.tencent.qqnt.kernel.nativeinterface;

public final class AlbumOpInfo {
    public String albumSourceKey = "";
    public int lastDeleteCount;
    public int lastDeleteTime;
    public boolean prohibitModifyAlbumType;
    public boolean recentlyUpdated;

    public String getAlbumSourceKey() {
        return this.albumSourceKey;
    }

    public int getLastDeleteCount() {
        return this.lastDeleteCount;
    }

    public int getLastDeleteTime() {
        return this.lastDeleteTime;
    }

    public boolean getProhibitModifyAlbumType() {
        return this.prohibitModifyAlbumType;
    }

    public boolean getRecentlyUpdated() {
        return this.recentlyUpdated;
    }
}
