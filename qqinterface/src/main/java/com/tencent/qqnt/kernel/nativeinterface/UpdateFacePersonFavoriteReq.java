package com.tencent.qqnt.kernel.nativeinterface;

public final class UpdateFacePersonFavoriteReq {
    public boolean isFavorite;
    public int seq;
    public boolean updateFavorite;
    public boolean updateRemark;
    public BusiInfo busiInfo = new BusiInfo();
    public String facePersonId = "";
    public String remark = "";

    public BusiInfo getBusiInfo() {
        return this.busiInfo;
    }

    public String getFacePersonId() {
        return this.facePersonId;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public String getRemark() {
        return this.remark;
    }

    public int getSeq() {
        return this.seq;
    }

    public boolean getUpdateFavorite() {
        return this.updateFavorite;
    }

    public boolean getUpdateRemark() {
        return this.updateRemark;
    }
}
