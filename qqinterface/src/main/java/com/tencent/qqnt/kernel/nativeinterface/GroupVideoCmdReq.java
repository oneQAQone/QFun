package com.tencent.qqnt.kernel.nativeinterface;

public final class GroupVideoCmdReq {
    public int cmdType;
    public byte[] pbBuf;

    public GroupVideoCmdReq() {
        this.pbBuf = new byte[0];
    }

    public int getCmdType() {
        return this.cmdType;
    }

    public byte[] getPbBuf() {
        return this.pbBuf;
    }

    public GroupVideoCmdReq(int i, byte[] bArr) {
        this.cmdType = i;
        this.pbBuf = bArr;
    }
}
