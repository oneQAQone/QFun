package com.tencent.qqnt.kernel.nativeinterface;

public class ReplyMsgMainInfo {
    public int chatType;
    public long msgId;
    public long msgSeq;
    public int msgTime;
    public long replyClientSeq;
    public long replyMsgSeq;
    public int replyMsgTime;

    public int getChatType() {
        return this.chatType;
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

    public long getReplyClientSeq() {
        return this.replyClientSeq;
    }

    public long getReplyMsgSeq() {
        return this.replyMsgSeq;
    }

    public int getReplyMsgTime() {
        return this.replyMsgTime;
    }
}
