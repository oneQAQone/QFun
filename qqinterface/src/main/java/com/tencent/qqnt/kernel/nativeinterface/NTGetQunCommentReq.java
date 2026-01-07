package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetQunCommentReq {
    public String albumId;
    public String attachInfo;
    public String batchId;
    public int commentCount;
    public StCommonExt ext;
    public String feedId;
    public String lloc;
    public String qunId;
    public RequestTimelineInfo requestTimeLine;
    public int seq;

    public NTGetQunCommentReq() {
        this.requestTimeLine = new RequestTimelineInfo();
        this.ext = new StCommonExt();
        this.qunId = "";
        this.feedId = "";
        this.attachInfo = "";
        this.albumId = "";
        this.batchId = "";
        this.lloc = "";
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public int getCommentCount() {
        return this.commentCount;
    }

    public StCommonExt getExt() {
        return this.ext;
    }

    public String getFeedId() {
        return this.feedId;
    }

    public String getLloc() {
        return this.lloc;
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

    public NTGetQunCommentReq(int i, RequestTimelineInfo requestTimelineInfo, StCommonExt stCommonExt, String str, String str2, int i2, String str3, String str4, String str5, String str6) {
        this.requestTimeLine = new RequestTimelineInfo();
        this.seq = i;
        this.requestTimeLine = requestTimelineInfo;
        this.ext = stCommonExt;
        this.qunId = str;
        this.feedId = str2;
        this.commentCount = i2;
        this.attachInfo = str3;
        this.albumId = str4;
        this.batchId = str5;
        this.lloc = str6;
    }
}
