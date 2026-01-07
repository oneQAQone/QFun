package com.tencent.qqnt.kernel.nativeinterface;

public interface IKernelAVSDKListener {
    void OnGroupVideoActionToAVSDK(int i, String str);

    void OnGroupVideoMemNumPushInfo(GroupVideoMemNumPushInfo groupVideoMemNumPushInfo);

    void OnGroupVideoServerPushToAVSDK(int i, byte[] bArr);

    void OnInviteActionToAVSDK(InviteInfo inviteInfo, int i, String str);

    void onActionToAVSDK(int i, String str);

    void onGroupAudioMemNumChange(GroupAudioMemNumChangeNotifyInfo groupAudioMemNumChangeNotifyInfo);

    void onGroupVideoInviteMemberUpdate(GroupVideoInviteMemberUpdateNotifyInfo groupVideoInviteMemberUpdateNotifyInfo);

    void onRecvGroupVideoJsonBufferRsp(int i, int i2, String str, String str2);
}
