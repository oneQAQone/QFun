package com.tencent.qqnt.kernel.nativeinterface;

public final class BusiInfo {
    public int busiType;
    public String ownerId = "";

    public int getBusiType() {
        return this.busiType;
    }

    public String getOwnerId() {
        return this.ownerId;
    }
}
