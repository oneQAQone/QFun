package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetQunRightRsp {
    public boolean normalUpload;
    public int result;
    public int seq;
    public String errMs = "";
    public QunRight right = new QunRight();
    public String traceid = "";
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();

    public String getErrMs() {
        return this.errMs;
    }

    public boolean getNormalUpload() {
        return this.normalUpload;
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

    public String getTraceid() {
        return this.traceid;
    }
}
