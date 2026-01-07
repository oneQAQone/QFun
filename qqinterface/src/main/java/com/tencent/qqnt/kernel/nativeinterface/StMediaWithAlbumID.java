package com.tencent.qqnt.kernel.nativeinterface;

public final class StMediaWithAlbumID {
    public String albumId = "";
    public StMedia media = new StMedia();

    public String getAlbumId() {
        return this.albumId;
    }

    public StMedia getMedia() {
        return this.media;
    }
}
