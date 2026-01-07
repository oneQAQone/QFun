package com.tencent.qqnt.kernel.nativeinterface;

public final class AccountInfo {
    public OpenidAccInfo openid;
    public String phoneNum;
    public String uid;
    public Long uin;

    public OpenidAccInfo getOpenid() {
        return this.openid;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public String getUid() {
        return this.uid;
    }

    public Long getUin() {
        return this.uin;
    }
}
