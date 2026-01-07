package com.tencent.qqnt.kernel.nativeinterface;

public final class AddBuddyReq {
    public int addFriendSetting;
    public String answer;
    public Integer autoSend;
    public Boolean bSupportAddRelief;
    public Boolean bSupportSecureTips;
    public AddBuddyDeviceInfo deviceInfo;
    public byte[] extInfo;
    public byte[] friendSrcDesc;
    public boolean isContactFriend;
    public Integer myAllowFlag;
    public int myFriendGroupId;
    public byte[] name;
    public byte[] name1;
    public byte[] permissionInfo;
    public byte[] remark;
    public byte[] secSign;
    public byte showMyCard;
    public byte[] sig;
    public int sourceId;
    public int sourceSubId;
    public byte[] srcDescription;
    public Integer srcFlag;
    public AccountInfo targetInfo = new AccountInfo();
    public byte[] token;
    public byte[] verify;

    public int getAddFriendSetting() {
        return this.addFriendSetting;
    }

    public String getAnswer() {
        return this.answer;
    }

    public Integer getAutoSend() {
        return this.autoSend;
    }

    public Boolean getBSupportAddRelief() {
        return this.bSupportAddRelief;
    }

    public Boolean getBSupportSecureTips() {
        return this.bSupportSecureTips;
    }

    public AddBuddyDeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }

    public byte[] getExtInfo() {
        return this.extInfo;
    }

    public byte[] getFriendSrcDesc() {
        return this.friendSrcDesc;
    }

    public boolean getIsContactFriend() {
        return this.isContactFriend;
    }

    public Integer getMyAllowFlag() {
        return this.myAllowFlag;
    }

    public int getMyFriendGroupId() {
        return this.myFriendGroupId;
    }

    public byte[] getName() {
        return this.name;
    }

    public byte[] getName1() {
        return this.name1;
    }

    public byte[] getPermissionInfo() {
        return this.permissionInfo;
    }

    public byte[] getRemark() {
        return this.remark;
    }

    public byte[] getSecSign() {
        return this.secSign;
    }

    public byte getShowMyCard() {
        return this.showMyCard;
    }

    public byte[] getSig() {
        return this.sig;
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getSourceSubId() {
        return this.sourceSubId;
    }

    public byte[] getSrcDescription() {
        return this.srcDescription;
    }

    public Integer getSrcFlag() {
        return this.srcFlag;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }

    public byte[] getToken() {
        return this.token;
    }

    public byte[] getVerify() {
        return this.verify;
    }
}
