package com.tencent.qqnt.kernel.nativeinterface;

public final class PrologueMsgElement {
    public String text;

    public PrologueMsgElement() {
        this.text = "";
    }

    public String getText() {
        return this.text;
    }

    public PrologueMsgElement(String str) {
        this.text = str;
    }
}
