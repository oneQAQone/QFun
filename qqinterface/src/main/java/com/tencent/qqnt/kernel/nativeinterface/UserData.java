package com.tencent.qqnt.kernel.nativeinterface;

public final class UserData {
    public boolean isFavorite;
    public String remark = "";

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public String getRemark() {
        return this.remark;
    }
}
