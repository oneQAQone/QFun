package com.tencent.qqnt.kernel.nativeinterface;

public final class SharedMsgInfo {
    public boolean isSharedMsg;

    public SharedMsgInfo() {
    }

    public boolean getIsSharedMsg() {
        return this.isSharedMsg;
    }

    public SharedMsgInfo(boolean z) {
        this.isSharedMsg = z;
    }
}
