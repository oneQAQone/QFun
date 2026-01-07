package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetMediaListTailTabReq {
    public String currentAlbumId;
    public String qunId;
    public RequestTimelineInfo requestTimeLine;
    public int seq;

    public NTGetMediaListTailTabReq() {
        this.requestTimeLine = new RequestTimelineInfo();
        this.qunId = "";
        this.currentAlbumId = "";
    }

    public String getCurrentAlbumId() {
        return this.currentAlbumId;
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

    public NTGetMediaListTailTabReq(int i, RequestTimelineInfo requestTimelineInfo, String str, String str2) {
        new RequestTimelineInfo();
        this.seq = i;
        this.requestTimeLine = requestTimelineInfo;
        this.qunId = str;
        this.currentAlbumId = str2;
    }
}
