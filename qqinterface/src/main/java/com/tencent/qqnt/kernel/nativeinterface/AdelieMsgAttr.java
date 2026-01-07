package com.tencent.qqnt.kernel.nativeinterface;

public final class AdelieMsgAttr {
    public byte[] msgBotBuf;
    public long msgDirection;
    public long msgPushStatus;

    public AdelieMsgAttr() {
    }

    public byte[] getMsgBotBuf() {
        return this.msgBotBuf;
    }

    public long getMsgDirection() {
        return this.msgDirection;
    }

    public long getMsgPushStatus() {
        return this.msgPushStatus;
    }

    public AdelieMsgAttr(long j, long j2, byte[] bArr) {
        this.msgPushStatus = j;
        this.msgDirection = j2;
        this.msgBotBuf = bArr;
    }
}
