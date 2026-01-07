package com.tencent.qqnt.kernel.nativeinterface;

public interface IKernelAVSDKService {
    void addKernelAVSDKListener(IKernelAVSDKListener iKernelAVSDKListener);

    void allowAlbumNotify();

    void removeKernelAVSDKListener(IKernelAVSDKListener iKernelAVSDKListener);

    void sendGroupVideoJsonBuffer(int i, String str);

    void setActionFromAVSDK(int i, byte[] bArr);

    void startGroupVideoCmdRequestFromAVSDK(GroupVideoCmdReq groupVideoCmdReq, IGroupVideoCmdRequestRsp iGroupVideoCmdRequestRsp);
}
