package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class BuddySettingRsp {
    public int errorCode;
    public boolean isContactFriend;
    public byte[] name;
    public byte[] name1;
    public int querySetting;
    public int result;
    public AccountInfo targetInfo = new AccountInfo();
    public ArrayList<String> question = new ArrayList<>();

    public int getErrorCode() {
        return this.errorCode;
    }

    public boolean getIsContactFriend() {
        return this.isContactFriend;
    }

    public byte[] getName() {
        return this.name;
    }

    public byte[] getName1() {
        return this.name1;
    }

    public int getQuerySetting() {
        return this.querySetting;
    }

    public ArrayList<String> getQuestion() {
        return this.question;
    }

    public int getResult() {
        return this.result;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }
}
