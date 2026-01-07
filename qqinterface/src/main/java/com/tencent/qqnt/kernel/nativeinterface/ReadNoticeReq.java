package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class ReadNoticeReq {
    public BusiInfo busiInfo = new BusiInfo();
    public ArrayList<String> facePersonIds = new ArrayList<>();
    public int seq;

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public ArrayList<String> getFacePersonIds() {
        return this.facePersonIds;
    }

    public int getSeq() {
        return this.seq;
    }
}
