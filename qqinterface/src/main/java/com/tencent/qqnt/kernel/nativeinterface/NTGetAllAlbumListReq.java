package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetAllAlbumListReq {
    public String attachInfo;
    public boolean canReadCache;
    public boolean enableCache;
    public RequestTimelineInfo requestTimeLine;
    public int seq;

    public NTGetAllAlbumListReq() {
        this.requestTimeLine = new RequestTimelineInfo();
        this.attachInfo = "";
    }

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public boolean getCanReadCache() {
        return this.canReadCache;
    }

    public boolean getEnableCache() {
        return this.enableCache;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getSeq() {
        return this.seq;
    }

    public NTGetAllAlbumListReq(int i, RequestTimelineInfo requestTimelineInfo, String str) {
        this.seq = i;
        this.requestTimeLine = requestTimelineInfo;
        this.attachInfo = str;
    }
}
