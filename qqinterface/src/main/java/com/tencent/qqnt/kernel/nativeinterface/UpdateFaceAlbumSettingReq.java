package com.tencent.qqnt.kernel.nativeinterface;

public final class UpdateFaceAlbumSettingReq {
    public BusiInfo busiInfo = new BusiInfo();
    public boolean faceClusteringEnabled;
    public int seq;
    public boolean updateFaceClusteringEnabled;

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public boolean getFaceClusteringEnabled() {
        return this.faceClusteringEnabled;
    }

    public int getSeq() {
        return this.seq;
    }

    public boolean getUpdateFaceClusteringEnabled() {
        return this.updateFaceClusteringEnabled;
    }
}
