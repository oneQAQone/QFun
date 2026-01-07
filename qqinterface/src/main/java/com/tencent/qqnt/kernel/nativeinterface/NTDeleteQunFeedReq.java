package com.tencent.qqnt.kernel.nativeinterface;

public final class NTDeleteQunFeedReq {
    public StCommonExt extInfo;
    public StFeed feed;
    public boolean isDeletePhoto;
    public RequestTimelineInfo requestTimeLine;
    public int seq;

    public NTDeleteQunFeedReq() {
        this.requestTimeLine = new RequestTimelineInfo();
        this.extInfo = new StCommonExt();
        this.feed = new StFeed();
    }

    public StCommonExt getExtInfo() {
        return this.extInfo;
    }

    public StFeed getFeed() {
        return this.feed;
    }

    public boolean getIsDeletePhoto() {
        return this.isDeletePhoto;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setExtInfo(StCommonExt stCommonExt) {
        this.extInfo = stCommonExt;
    }

    public void setFeed(StFeed stFeed) {
        this.feed = stFeed;
    }

    public void setIsDeletePhoto(boolean z) {
        this.isDeletePhoto = z;
    }

    public void setRequestTimeLine(RequestTimelineInfo requestTimelineInfo) {
        this.requestTimeLine = requestTimelineInfo;
    }

    public void setSeq(int i) {
        this.seq = i;
    }

    public NTDeleteQunFeedReq(int i, RequestTimelineInfo requestTimelineInfo, StCommonExt stCommonExt, StFeed stFeed, boolean z) {
        this.requestTimeLine = new RequestTimelineInfo();
        this.extInfo = new StCommonExt();
        this.seq = i;
        this.requestTimeLine = requestTimelineInfo;
        this.extInfo = stCommonExt;
        this.feed = stFeed;
        this.isDeletePhoto = z;
    }
}
