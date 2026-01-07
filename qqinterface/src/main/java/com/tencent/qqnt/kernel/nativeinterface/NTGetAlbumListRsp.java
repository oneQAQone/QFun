package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTGetAlbumListRsp {
    public boolean hasMore;
    public boolean isFromCache;
    public int result;
    public int seq;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public ArrayList<AlbumInfo> albumList = new ArrayList<>();
    public String attachInfo = "";
    public QunRight right = new QunRight();
    public StBanner banner = new StBanner();
    public FaceAlbumCover faceAlbumCover = new FaceAlbumCover();

    public ArrayList<AlbumInfo> getAlbumList() {
        return this.albumList;
    }

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public StBanner getBanner() {
        return this.banner;
    }

    public String getErrMs() {
        return this.errMs;
    }

    public FaceAlbumCover getFaceAlbumCover() {
        return this.faceAlbumCover;
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
