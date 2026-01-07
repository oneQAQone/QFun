package com.tencent.qqnt.kernel.nativeinterface;

public final class GroupVideoCmdRsp {
    public byte[] pbBuf;

    public GroupVideoCmdRsp() {
        this.pbBuf = new byte[0];
    }

    public byte[] getPbBuf() {
        return this.pbBuf;
    }

    public GroupVideoCmdRsp(byte[] bArr) {
        this.pbBuf = bArr;
    }
}
