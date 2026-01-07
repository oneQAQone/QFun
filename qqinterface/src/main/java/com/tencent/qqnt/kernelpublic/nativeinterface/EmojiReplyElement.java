package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class EmojiReplyElement implements Serializable {
    public int emojiId;
    public int emojiType;
    public long msgId;
    public long msgSeq;
    long serialVersionUID = 1;
    public long tinyId;

    public EmojiReplyElement() {
    }

    public int getEmojiId() {
        return this.emojiId;
    }

    public int getEmojiType() {
        return this.emojiType;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public long getMsgSeq() {
        return this.msgSeq;
    }

    public long getTinyId() {
        return this.tinyId;
    }

    public EmojiReplyElement(long j, long j2, long j3, int i, int i2) {
        this.tinyId = j;
        this.msgSeq = j2;
        this.msgId = j3;
        this.emojiId = i;
        this.emojiType = i2;
    }
}
