package com.tencent.qqnt.kernel.nativeinterface;

public interface IGetSmartInfoCallback {
    void onResult(int i, String str, byte[] bArr, SmartReq smartReq, SmartRsp smartRsp);
}
