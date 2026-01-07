package com.tencent.qqnt.kernel.nativeinterface;

public final class BuddyInfoRsp {
    public AccountInfo targetInfo = new AccountInfo();
    public String nick = "";

    public String getNick() {
        return this.nick;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }
}
