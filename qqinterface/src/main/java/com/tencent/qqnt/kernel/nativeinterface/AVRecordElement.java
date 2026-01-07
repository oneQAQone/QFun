package com.tencent.qqnt.kernel.nativeinterface;

public final class AVRecordElement {
    public Integer extraType;
    public boolean hasRead;
    public int mainType;
    public String text;
    public long time;
    public int type;

    public AVRecordElement() {
        this.text = "";
    }

    public Integer getExtraType() {
        return this.extraType;
    }

    public boolean getHasRead() {
        return this.hasRead;
    }

    public int getMainType() {
        return this.mainType;
    }

    public String getText() {
        return this.text;
    }

    public long getTime() {
        return this.time;
    }

    public int getType() {
        return this.type;
    }

    public AVRecordElement(int i, long j, String str, int i2, boolean z, Integer num) {
        this.type = i;
        this.time = j;
        this.text = str;
        this.mainType = i2;
        this.hasRead = z;
        this.extraType = num;
    }
}