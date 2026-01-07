package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetFeedByIdRsp {
    public int result;
    public int seq;
    public String errMs = "";
    public String traceid = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public StCommonExt ext = new StCommonExt();
    public ClientFeed feed = new ClientFeed();

    public String getErrMs() {
        return this.errMs;
    }

    public StCommonExt getExt() {
        return this.ext;
    }

    public ClientFeed getFeed() {
        return this.feed;
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

    public String getTraceid() {
        return this.traceid;
    }
}
