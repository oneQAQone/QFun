package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetQunFeedDetailRsp {
    public boolean hasmore;
    public int result;
    public int seq;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public StCommonExt extInfo = new StCommonExt();
    public ClientFeed feed = new ClientFeed();
    public String attachInfo = "";
    public String mediaAttach = "";
    public QunRight right = new QunRight();
    public GetFeedFacePersonInfoRsp feedFacePersonInfo = new GetFeedFacePersonInfoRsp();

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public String getErrMs() {
        return this.errMs;
    }

    public StCommonExt getExtInfo() {
        return this.extInfo;
    }

    public ClientFeed getFeed() {
        return this.feed;
    }

    public GetFeedFacePersonInfoRsp getFeedFacePersonInfo() {
        return this.feedFacePersonInfo;
    }

    public boolean getHasmore() {
        return this.hasmore;
    }

    public String getMediaAttach() {
        return this.mediaAttach;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getResult() {
        return this.result;
    }

    public QunRight getRight() {
        return this.right;
    }

    public int getSeq() {
        return this.seq;
    }

    public String getTraceId() {
        return this.traceId;
    }
}
