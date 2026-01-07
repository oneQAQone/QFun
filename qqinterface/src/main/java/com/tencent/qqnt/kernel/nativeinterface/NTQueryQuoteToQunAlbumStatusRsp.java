package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NTQueryQuoteToQunAlbumStatusRsp {
    public int result;
    public int seq;
    public String errMs = "";
    public String traceId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public ArrayList<StatusUnit> statusUnits = new ArrayList<>();

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

    public ArrayList<StatusUnit> getStatusUnits() {
        return this.statusUnits;
    }

    public String getTraceId() {
        return this.traceId;
    }
}
