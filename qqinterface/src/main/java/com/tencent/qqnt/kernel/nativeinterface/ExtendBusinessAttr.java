package com.tencent.qqnt.kernel.nativeinterface;

public final class ExtendBusinessAttr {
    public byte[] buffer;
    public int type;

    public ExtendBusinessAttr() {
        this.buffer = new byte[0];
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getType() {
        return this.type;
    }

    public ExtendBusinessAttr(int i, byte[] bArr) {
        this.type = i;
        this.buffer = bArr;
    }
}
