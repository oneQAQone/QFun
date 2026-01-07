package com.tencent.qqnt.kernel.nativeinterface;

public final class NTGetMediaListReq {
    public long batchId;
    public int downloadStatus;
    public boolean needDownloadedMask;
    public int seq;
    public RequestTimelineInfo requestTimeLine = new RequestTimelineInfo();
    public String qunId = "";
    public String albumId = "";
    public String lloc = "";
    public String attachInfo = "";

    public String getAlbumId() {
        return this.albumId;
    }

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public long getBatchId() {
        return this.batchId;
    }

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public String getLloc() {
        return this.lloc;
    }

    public boolean getNeedDownloadedMask() {
        return this.needDownloadedMask;
    }

    public String getQunId() {
        return this.qunId;
    }

    public RequestTimelineInfo getRequestTimeLine() {
        return this.requestTimeLine;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setAlbumId(String str) {
        this.albumId = str;
    }

    public void setAttachInfo(String str) {
        this.attachInfo = str;
    }

    public void setBatchId(long j) {
        this.batchId = j;
    }

    public void setDownloadStatus(int i) {
        this.downloadStatus = i;
    }

    public void setLloc(String str) {
        this.lloc = str;
    }

    public void setNeedDownloadedMask(boolean z) {
        this.needDownloadedMask = z;
    }

    public void setQunId(String str) {
        this.qunId = str;
    }

    public void setRequestTimeLine(RequestTimelineInfo requestTimelineInfo) {
        this.requestTimeLine = requestTimelineInfo;
    }

    public void setSeq(int i) {
        this.seq = i;
    }
}
