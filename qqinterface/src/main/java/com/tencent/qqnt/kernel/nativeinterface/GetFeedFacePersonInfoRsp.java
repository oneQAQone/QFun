package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GetFeedFacePersonInfoRsp {
    public long favoriteFacePersonCount;
    public int result;
    public int seq;
    public long totalFacePersonCount;
    public long totalMediaCount;
    public String errMsg = "";
    public ArrayList<FacePerson> facePersons = new ArrayList<>();
    public String text = "";
    public String jumpSchemeUrl = "";

    public String getErrMsg() {
        return this.errMsg;
    }

    public ArrayList<FacePerson> getFacePersons() {
        return this.facePersons;
    }

    public long getFavoriteFacePersonCount() {
        return this.favoriteFacePersonCount;
    }

    public String getJumpSchemeUrl() {
        return this.jumpSchemeUrl;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }

    public String getText() {
        return this.text;
    }

    public long getTotalFacePersonCount() {
        return this.totalFacePersonCount;
    }

    public long getTotalMediaCount() {
        return this.totalMediaCount;
    }
}
