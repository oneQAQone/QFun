package com.tencent.qqnt.kernel.nativeinterface;

public final class BytesEntry {
    public String key = "";
    public byte[] value = new byte[0];

    public String getKey() {
        return this.key;
    }

    public byte[] getValue() {
        return this.value;
    }
}
