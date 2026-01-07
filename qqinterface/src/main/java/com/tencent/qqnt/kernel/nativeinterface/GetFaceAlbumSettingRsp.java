package com.tencent.qqnt.kernel.nativeinterface;

public final class GetFaceAlbumSettingRsp {
    public boolean faceClusteringEnabled;
    public int result;
    public int seq;
    public String errMsg = "";
    public String noMeaning = "";

    public String getErrMsg() {
        return this.errMsg;
    }

    public boolean getFaceClusteringEnabled() {
        return this.faceClusteringEnabled;
    }

    public String getNoMeaning() {
        return this.noMeaning;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }
}
