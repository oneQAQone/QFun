package com.tencent.qqnt.kernel.nativeinterface;

public interface IBuddySettingCallback {
    void onResult(int i, String str, byte[] bArr, BuddySettingReq buddySettingReq, BuddySettingRsp buddySettingRsp);
}
