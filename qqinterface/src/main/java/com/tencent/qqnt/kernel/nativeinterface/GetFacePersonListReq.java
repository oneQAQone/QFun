package com.tencent.qqnt.kernel.nativeinterface;

public final class GetFacePersonListReq {
    public boolean canReadCache;
    public boolean enableCache;
    public int seq;
    public BusiInfo busiInfo = new BusiInfo();
    public String pageCookie = "";
    public String albumId = "";
    public AlbumFaceAvatarResize faceAvatarResize = new AlbumFaceAvatarResize();

    public String getAlbumId() {
        return this.albumId;
    }

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public boolean getCanReadCache() {
        return this.canReadCache;
    }

    public boolean getEnableCache() {
        return this.enableCache;
    }

    public AlbumFaceAvatarResize getFaceAvatarResize() {
        return this.faceAvatarResize;
    }

    public String getPageCookie() {
        return this.pageCookie;
    }

    public int getSeq() {
        return this.seq;
    }
}
