package com.tencent.qqnt.kernel.nativeinterface;

public final class CloseNewFeatureIntroReq {
    public BusiInfo busiInfo = new BusiInfo();
    public int seq;

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public int getSeq() {
        return this.seq;
    }
}
