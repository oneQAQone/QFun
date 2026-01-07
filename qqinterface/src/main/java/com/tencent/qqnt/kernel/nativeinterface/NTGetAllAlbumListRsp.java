package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTGetAllAlbumListRsp {
    public String attachInfo;
    public String errMs;
    public boolean hasMore;
    public boolean isFromCache;
    public ArrayList<GetAllAlbumListItem> qunList;
    public RequestTimelineInfo requestTimeLine;
    public int result;
    public int seq;
    public String traceId;

    public NTGetAllAlbumListRsp() {
        this.errMs = "";
        this.traceId = "";
        this.requestTimeLine = new RequestTimelineInfo();
        this.qunList = new ArrayList<>();
        this.attachInfo = "";
    }

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public String getErrMs() {
        return this.errMs;
    }

    public boolean getHasMore() {
        return this.hasMore;
    }

    public boolean getIsFromCache() {
        return this.isFromCache;
    }

    public ArrayList<GetAllAlbumListItem> getQunList() {
        return this.qunList;
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

    public NTGetAllAlbumListRsp(int i, int i2, String str, String str2, RequestTimelineInfo requestTimelineInfo, ArrayList<GetAllAlbumListItem> arrayList, String str3, boolean z) {
        this.errMs = "";
        this.traceId = "";
        this.requestTimeLine = new RequestTimelineInfo();
        this.seq = i;
        this.result = i2;
        this.errMs = str;
        this.traceId = str2;
        this.requestTimeLine = requestTimelineInfo;
        this.qunList = arrayList;
        this.attachInfo = str3;
        this.hasMore = z;
    }
}
