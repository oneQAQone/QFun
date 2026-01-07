package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class ProclamationElement implements Serializable {
    public int isSetProclamation;
    long serialVersionUID = 1;

    public ProclamationElement() {
    }

    public int getIsSetProclamation() {
        return this.isSetProclamation;
    }

    public ProclamationElement(int i) {
        this.isSetProclamation = i;
    }
}
