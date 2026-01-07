package com.tencent.qqnt.kernel.nativeinterface;

public final class ClientAttr {
    public StUser user = new StUser();
    public UinAttr attr = new UinAttr();

    public UinAttr getAttr() {
        return this.attr;
    }

    public StUser getUser() {
        return this.user;
    }
}
