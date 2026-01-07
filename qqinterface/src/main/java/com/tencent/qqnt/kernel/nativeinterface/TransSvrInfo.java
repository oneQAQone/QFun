package com.tencent.qqnt.kernel.nativeinterface;

public final class TransSvrInfo {
    public String errMsg;
    public int retCode;
    public int subType;
    public byte[] transInfo;

    public TransSvrInfo() {
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public int getRetCode() {
        return this.retCode;
    }

    public int getSubType() {
        return this.subType;
    }

    public byte[] getTransInfo() {
        return this.transInfo;
    }

    public TransSvrInfo(int i, int i2, String str, byte[] bArr) {
        this.subType = i;
        this.retCode = i2;
        this.errMsg = str;
        this.transInfo = bArr;
    }
}
