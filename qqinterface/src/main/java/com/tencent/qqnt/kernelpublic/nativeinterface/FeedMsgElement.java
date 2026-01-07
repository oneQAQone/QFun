package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class FeedMsgElement implements Serializable {
    public String content;
    long serialVersionUID;
    public long tinyId;

    public FeedMsgElement() {
        this.serialVersionUID = 1L;
        this.content = "";
    }

    public String getContent() {
        return this.content;
    }

    public long getTinyId() {
        return this.tinyId;
    }

    public FeedMsgElement(long j, String str) {
        this.serialVersionUID = 1L;
        this.tinyId = j;
        this.content = str;
    }
}
