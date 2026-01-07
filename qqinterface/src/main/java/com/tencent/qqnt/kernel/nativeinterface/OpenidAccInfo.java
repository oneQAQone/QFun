package com.tencent.qqnt.kernel.nativeinterface;

public final class OpenidAccInfo {
    public int appid;
    public String openid = "";

    public int getAppid() {
        return this.appid;
    }

    public String getOpenid() {
        return this.openid;
    }
}
