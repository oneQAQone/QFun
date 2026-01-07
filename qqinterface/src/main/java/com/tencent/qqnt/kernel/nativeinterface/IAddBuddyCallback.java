package com.tencent.qqnt.kernel.nativeinterface;

public interface IAddBuddyCallback {
    void onResult(int i, String str, byte[] bArr, AddBuddyReq addBuddyReq, AddBuddyRsp addBuddyRsp);
}
