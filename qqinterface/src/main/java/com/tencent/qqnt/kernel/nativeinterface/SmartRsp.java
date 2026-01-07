package com.tencent.qqnt.kernel.nativeinterface;

public final class SmartRsp {
    public int groupId;
    public AccountInfo targetInfo = new AccountInfo();
    public String remark = "";

    public int getGroupId() {
        return this.groupId;
    }

    public String getRemark() {
        return this.remark;
    }

    public AccountInfo getTargetInfo() {
        return this.targetInfo;
    }
}
