package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTGetMediaListTailTabRsp {
    public ArrayList<AlbumInfo> albums;
    public String errMs;
    public RequestTimelineInfo requestTimeLine;
    public int result;
    public int seq;
    public String traceId;

    public NTGetMediaListTailTabRsp() {
        this.errMs = "";
        this.traceId = "";
        this.requestTimeLine = new RequestTimelineInfo();
        this.albums = new ArrayList<>();
    }

    public ArrayList<AlbumInfo> getAlbums() {
        return this.albums;
    }

    public String getErrMs() {
        return this.errMs;
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

    public NTGetMediaListTailTabRsp(int i, int i2, String str, String str2, RequestTimelineInfo requestTimelineInfo, ArrayList<AlbumInfo> arrayList) {
        this.errMs = "";
        this.traceId = "";
        this.requestTimeLine = new RequestTimelineInfo();
        this.seq = i;
        this.result = i2;
        this.errMs = str;
        this.traceId = str2;
        this.requestTimeLine = requestTimelineInfo;
        this.albums = arrayList;
    }
}
