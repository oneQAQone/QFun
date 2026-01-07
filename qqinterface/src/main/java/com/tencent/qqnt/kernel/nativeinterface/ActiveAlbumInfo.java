package com.tencent.qqnt.kernel.nativeinterface;

public final class ActiveAlbumInfo {
    public boolean isActiveAlbum;
    public String jumpUrl = "";

    public boolean getIsActiveAlbum() {
        return this.isActiveAlbum;
    }

    public String getJumpUrl() {
        return this.jumpUrl;
    }
}
