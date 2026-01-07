package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTGetMediaListRsp {
    public int filterMaskMaxPullCount;
    public int mediaIndex;
    public boolean nextHasMore;
    public boolean prevHasMore;
    public int result;
    public int seq;
    public int totalCount;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public AlbumInfo album = new AlbumInfo();
    public ArrayList<BatchInfo> batchList = new ArrayList<>();
    public ArrayList<StMedia> mediaList = new ArrayList<>();
    public String prevAttachInfo = "";
    public String nextAttachInfo = "";
    public QunRight right = new QunRight();
    public FaceAlbumInfo faceAlbumInfo = new FaceAlbumInfo();

    public AlbumInfo getAlbum() {
        return this.album;
    }

    public ArrayList<BatchInfo> getBatchList() {
        return this.batchList;
    }

    public String getErrMs() {
        return this.errMs;
    }

    public FaceAlbumInfo getFaceAlbumInfo() {
        return this.faceAlbumInfo;
    }

    public int getFilterMaskMaxPullCount() {
        return this.filterMaskMaxPullCount;
    }

    public int getMediaIndex() {
        return this.mediaIndex;
    }

    public ArrayList<StMedia> getMediaList() {
        return this.mediaList;
    }

    public String getNextAttachInfo() {
        return this.nextAttachInfo;
    }

    public boolean getNextHasMore() {
        return this.nextHasMore;
    }

    public String getPrevAttachInfo() {
        return this.prevAttachInfo;
    }

    public boolean getPrevHasMore() {
        return this.prevHasMore;
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

    public int getTotalCount() {
        return this.totalCount;
    }

    public String getTraceId() {
        return this.traceId;
    }
}
