package com.tencent.qqnt.kernel.nativeinterface;

public interface IGetBuddyInfoCallback {
    void onResult(int i, String str, byte[] bArr, AccountInfo accountInfo, BuddyInfoRsp buddyInfoRsp);
}
