package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetQunRightReq {
    public String qunId = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public int seq;

    public String getQunId() {
        return this.qunId;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getSeq() {
        return this.seq;
    }
}
