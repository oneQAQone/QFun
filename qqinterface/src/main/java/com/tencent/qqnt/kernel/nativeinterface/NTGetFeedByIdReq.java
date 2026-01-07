package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetFeedByIdReq {
    public int seq;
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public StCommonExt ext = new StCommonExt();
    public String qunId = "";
    public String feedId = "";
    public String albumId = "";
    public String batchId = "";

    public String getAlbumId() {
        return this.albumId;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public StCommonExt getExt() {
        return this.ext;
    }

    public String getFeedId() {
        return this.feedId;
    }

    public String getQunId() {
        return this.qunId;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getSeq() {
        return this.seq;
    }
}
