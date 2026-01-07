package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GetFeedFacePersonInfoDetailRsp {
    public long favoriteFacePersonCount;
    public boolean isEnd;
    public int result;
    public int seq;
    public long totalFacePersonCount;
    public String errMsg = "";
    public ArrayList<FacePerson> favoriteFacePersons = new ArrayList<>();
    public ArrayList<FacePerson> otherFacePersons = new ArrayList<>();
    public String pageCookie = "";

    public String getErrMsg() {
        return this.errMsg;
    }

    public long getFavoriteFacePersonCount() {
        return this.favoriteFacePersonCount;
    }

    public ArrayList<FacePerson> getFavoriteFacePersons() {
        return this.favoriteFacePersons;
    }

    public boolean getIsEnd() {
        return this.isEnd;
    }

    public ArrayList<FacePerson> getOtherFacePersons() {
        return this.otherFacePersons;
    }

    public String getPageCookie() {
        return this.pageCookie;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }

    public long getTotalFacePersonCount() {
        return this.totalFacePersonCount;
    }
}
