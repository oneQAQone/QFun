package com.tencent.qqnt.kernel.nativeinterface;

public final class JsonGrayMsgInfo {
    public long cliSeq;
    public long msgId;
    public long msgRandom;
    public long msgSeq;
    public long msgTime;

    public JsonGrayMsgInfo() {
    }

    public long getCliSeq() {
        return this.cliSeq;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public long getMsgRandom() {
        return this.msgRandom;
    }

    public long getMsgSeq() {
        return this.msgSeq;
    }

    public long getMsgTime() {
        return this.msgTime;
    }

    public JsonGrayMsgInfo(long j, long j2, long j3, long j4, long j5) {
        this.msgId = j;
        this.msgSeq = j2;
        this.cliSeq = j3;
        this.msgTime = j4;
        this.msgRandom = j5;
    }
}
