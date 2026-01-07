package com.tencent.qqnt.kernel.nativeinterface;

import java.io.Serializable;

public final class Entry implements Serializable {
    public int numberKey;
    public String key = "";
    public String value = "";
    long serialVersionUID = 1;

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public int getNumberKey() {
        return this.numberKey;
    }

    public void setNumberKey(int i) {
        this.numberKey = i;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String str) {
        this.value = str;
    }
}
