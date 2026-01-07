package com.tencent.qqnt.kernel.nativeinterface;

public final class GetFeedFacePersonInfoDetailReq {
    public long batchUploadTime;
    public int seq;
    public BusiInfo busiInfo = new BusiInfo();
    public String albumId = "";
    public String batchId = "";
    public String getDetailAttachInfo = "";
    public String pageCookie = "";

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

    public String getGetDetailAttachInfo() {
        return this.getDetailAttachInfo;
    }

    public String getPageCookie() {
        return this.pageCookie;
    }

    public int getSeq() {
        return this.seq;
    }
}
