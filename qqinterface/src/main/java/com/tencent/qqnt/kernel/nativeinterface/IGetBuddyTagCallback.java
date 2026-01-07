package com.tencent.qqnt.kernel.nativeinterface;

public interface IGetBuddyTagCallback {
    void onResult(int i, String str, byte[] bArr, AccountInfo accountInfo, BuddyTagRsp buddyTagRsp);
}
