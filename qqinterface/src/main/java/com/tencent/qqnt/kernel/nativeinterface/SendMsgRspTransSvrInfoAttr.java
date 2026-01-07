package com.tencent.qqnt.kernel.nativeinterface;

public final class SendMsgRspTransSvrInfoAttr {
    public int rspCode;
    public int rspErrType;
    public TransSvrInfo transSvrInfo;

    public SendMsgRspTransSvrInfoAttr() {
    }

    public int getRspCode() {
        return this.rspCode;
    }

    public int getRspErrType() {
        return this.rspErrType;
    }

    public TransSvrInfo getTransSvrInfo() {
        return this.transSvrInfo;
    }

    public SendMsgRspTransSvrInfoAttr(int i, int i2, TransSvrInfo transSvrInfo) {
        this.rspCode = i;
        this.rspErrType = i2;
        this.transSvrInfo = transSvrInfo;
    }
}
