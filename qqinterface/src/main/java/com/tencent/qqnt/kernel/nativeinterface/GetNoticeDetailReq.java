package com.tencent.qqnt.kernel.nativeinterface;

public final class GetNoticeDetailReq {
    public BusiInfo busiInfo = new BusiInfo();
    public String noticeId = "";
    public int seq;

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public String getNoticeId() {
        return this.noticeId;
    }

    public int getSeq() {
        return this.seq;
    }
}
