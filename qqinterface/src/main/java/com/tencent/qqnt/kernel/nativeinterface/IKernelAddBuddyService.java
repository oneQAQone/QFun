package com.tencent.qqnt.kernel.nativeinterface;

public interface IKernelAddBuddyService {
    void addBuddy(String str, AddBuddyReq addBuddyReq, byte[] bArr, IAddBuddyCallback iAddBuddyCallback);

    void getAddBuddyRequestTag(String str, AccountInfo accountInfo, byte[] bArr, IGetBuddyTagCallback iGetBuddyTagCallback);

    void getBuddySetting(String str, BuddySettingReq buddySettingReq, byte[] bArr, IBuddySettingCallback iBuddySettingCallback);

    void getSmartInfo(String str, SmartReq smartReq, byte[] bArr, IGetSmartInfoCallback iGetSmartInfoCallback);

    void queryUinSafetyFlag(String str, AccountInfo accountInfo, byte[] bArr, IQueryUinSafetyFlagCallback iQueryUinSafetyFlagCallback);

    void requestInfoByAccount(String str, AccountInfo accountInfo, byte[] bArr, IGetBuddyInfoCallback iGetBuddyInfoCallback);
}
