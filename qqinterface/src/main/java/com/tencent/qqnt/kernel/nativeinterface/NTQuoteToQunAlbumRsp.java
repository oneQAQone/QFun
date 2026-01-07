package com.tencent.qqnt.kernel.nativeinterface;

public final class NTQuoteToQunAlbumRsp {
    public int queryStatusInterval;
    public int queryStatusMaxCount;
    public int result;
    public int seq;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();

    public String getErrMs() {
        return this.errMs;
    }

    public int getQueryStatusInterval() {
        return this.queryStatusInterval;
    }

    public int getQueryStatusMaxCount() {
        return this.queryStatusMaxCount;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }

    public String getTraceId() {
        return this.traceId;
    }
}
