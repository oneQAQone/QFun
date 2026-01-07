package com.tencent.qqnt.kernel.nativeinterface;

public final class PublicAccountAttrs {
    public Integer ack;
    public Long bitmap;
    public byte[] gdtCliData;
    public byte[] gdtImpData;
    public Integer op;
    public Long pubMsgId;
    public Integer report;
    public Integer showTime;
    public Long uniqueId;
    public byte[] viewId;

    public PublicAccountAttrs() {
    }

    public Integer getAck() {
        return this.ack;
    }

    public Long getBitmap() {
        return this.bitmap;
    }

    public byte[] getGdtCliData() {
        return this.gdtCliData;
    }

    public byte[] getGdtImpData() {
        return this.gdtImpData;
    }

    public Integer getOp() {
        return this.op;
    }

    public Long getPubMsgId() {
        return this.pubMsgId;
    }

    public Integer getReport() {
        return this.report;
    }

    public Integer getShowTime() {
        return this.showTime;
    }

    public Long getUniqueId() {
        return this.uniqueId;
    }

    public byte[] getViewId() {
        return this.viewId;
    }

    public PublicAccountAttrs(Long l, Long l2, Integer num, Integer num2, Integer num3, Integer num4, Long l3, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        this.pubMsgId = l;
        this.uniqueId = l2;
        this.op = num;
        this.showTime = num2;
        this.report = num3;
        this.ack = num4;
        this.bitmap = l3;
        this.gdtImpData = bArr;
        this.gdtCliData = bArr2;
        this.viewId = bArr3;
    }
}
