package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetAlbumInfoReq {
    public String albumId;
    public String qunId;
    public RequestTimelineInfo requestTimeLine;
    public int seq;

    public NTGetAlbumInfoReq() {
        this.requestTimeLine = new RequestTimelineInfo();
        this.qunId = "";
        this.albumId = "";
    }

    public String getAlbumId() {
        return this.albumId;
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

    public NTGetAlbumInfoReq(int i, RequestTimelineInfo requestTimelineInfo, String str, String str2) {
        this.seq = i;
        this.requestTimeLine = requestTimelineInfo;
        this.qunId = str;
        this.albumId = str2;
    }
}
