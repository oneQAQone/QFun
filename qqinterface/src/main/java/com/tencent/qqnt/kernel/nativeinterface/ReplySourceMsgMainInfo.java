package com.tencent.qqnt.kernel.nativeinterface;

public class ReplySourceMsgMainInfo {
    public int chatType;
    public long clientSeq;
    public long msgId;
    public long msgSeq;
    public int msgTime;
    public int sendStatus;
    public int sendType;

    public int getChatType() {
        return this.chatType;
    }

    public long getClientSeq() {
        return this.clientSeq;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public long getMsgSeq() {
        return this.msgSeq;
    }

    public int getMsgTime() {
        return this.msgTime;
    }

    public int getSendStatus() {
        return this.sendStatus;
    }

    public int getSendType() {
        return this.sendType;
    }
}
