package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class FileReceiptElement implements Serializable {
    public String fileName;
    long serialVersionUID;

    public FileReceiptElement() {
        this.serialVersionUID = 1L;
        this.fileName = "";
    }

    public String getFileName() {
        return this.fileName;
    }

    public FileReceiptElement(String str) {
        this.serialVersionUID = 1L;
        this.fileName = str;
    }
}
