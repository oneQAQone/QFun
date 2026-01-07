package com.tencent.qqnt.kernel.nativeinterface;

public final class GetFacePersonTimelineReq {
    public long batchUploadTime;
    public boolean canReadCache;
    public int downloadStatus;
    public boolean enableCache;
    public boolean needDownloadedMask;
    public int seq;
    public BusiInfo busiInfo = new BusiInfo();
    public String facePersonId = "";
    public String albumId = "";
    public String nextPageCookie = "";
    public String prevPageCookie = "";
    public String batchId = "";

    public String getAlbumId() {
        return this.albumId;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public long getBatchUploadTime() {
        return this.batchUploadTime;
    }

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public boolean getCanReadCache() {
        return this.canReadCache;
    }

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public boolean getEnableCache() {
        return this.enableCache;
    }

    public String getFacePersonId() {
        return this.facePersonId;
    }

    public boolean getNeedDownloadedMask() {
        return this.needDownloadedMask;
    }

    public String getNextPageCookie() {
        return this.nextPageCookie;
    }

    public String getPrevPageCookie() {
        return this.prevPageCookie;
    }

    public int getSeq() {
        return this.seq;
    }
}
