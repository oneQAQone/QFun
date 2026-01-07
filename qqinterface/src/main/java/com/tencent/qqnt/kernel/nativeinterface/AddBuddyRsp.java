package com.tencent.qqnt.kernel.nativeinterface;

public final class AddBuddyRsp {
    public int addFriendSetting;
    public Integer errorCode;
    public String errorString;
    public Integer groupId;
    public Integer myAllowFlag;
    public byte[] name;
    public byte[] name1;
    public int result;
    public byte[] sig;
    public AccountInfo targetInfo = new AccountInfo();
    public byte[] verify;

    public int getAddFriendSetting() {
        return this.addFriendSetting;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getErrorString() {
        return this.errorString;
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public Integer getMyAllowFlag() {
        return this.myAllowFlag;
    }

    public byte[] getName() {
        return this.name;
    }

    public byte[] getName1() {
        return this.name1;
    }

    public int getResult() {
        return this.result;
    }

    public byte[] getSig() {
        return this.sig;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }

    public byte[] getVerify() {
        return this.verify;
    }
}
