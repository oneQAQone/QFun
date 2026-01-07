package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class EssenceElement implements Serializable {
    public int isSetEssence;
    public long msgSeq;
    long serialVersionUID = 1;
    public long tinyId;

    public EssenceElement() {
    }

    public int getIsSetEssence() {
        return this.isSetEssence;
    }

    public long getMsgSeq() {
        return this.msgSeq;
    }

    public long getTinyId() {
        return this.tinyId;
    }

    public EssenceElement(long j, long j2, int i) {
        this.tinyId = j;
        this.msgSeq = j2;
        this.isSetEssence = i;
    }
}
