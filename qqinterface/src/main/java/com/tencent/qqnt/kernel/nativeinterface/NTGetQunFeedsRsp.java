package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTGetQunFeedsRsp {
    public boolean hasMore;
    public boolean isFromCache;
    public int result;
    public int seq;
    public int unreadFeedsNum;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public StCommonExt extInfo = new StCommonExt();
    public ArrayList<ClientFeed> feeds = new ArrayList<>();
    public String attachInfo = "";
    public StBanner banner = new StBanner();
    public GetFaceAlbumCoverRsp faceAlbumCover = new GetFaceAlbumCoverRsp();
    public FaceNoticeInfo faceNoticeInfo = new FaceNoticeInfo();

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public StBanner getBanner() {
        return this.banner;
    }

    public String getErrMs() {
        return this.errMs;
    }

    public StCommonExt getExtInfo() {
        return this.extInfo;
    }

    public GetFaceAlbumCoverRsp getFaceAlbumCover() {
        return this.faceAlbumCover;
    }

    public FaceNoticeInfo getFaceNoticeInfo() {
        return this.faceNoticeInfo;
    }

    public ArrayList<ClientFeed> getFeeds() {
        return this.feeds;
    }

    public boolean getHasMore() {
        return this.hasMore;
    }

    public boolean getIsFromCache() {
        return this.isFromCache;
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

    public int getUnreadFeedsNum() {
        return this.unreadFeedsNum;
    }
}
