package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GetNoticeDetailRsp {
    public String errMsg = "";
    public ArrayList<FacePerson> facePerson = new ArrayList<>();
    public int result;
    public int seq;

    public String getErrMsg() {
        return this.errMsg;
    }

    public ArrayList<FacePerson> getFacePerson() {
        return this.facePerson;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }
}
