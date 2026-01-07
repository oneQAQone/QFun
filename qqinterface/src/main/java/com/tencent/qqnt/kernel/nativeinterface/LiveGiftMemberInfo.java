package com.tencent.qqnt.kernel.nativeinterface;

public final class LiveGiftMemberInfo {
    public String nickName;
    public int roleColor;
    public long roleId;
    public String roleName;
    public String tinyId;

    public LiveGiftMemberInfo() {
        this.tinyId = "";
        this.nickName = "";
        this.roleName = "";
    }

    public String getNickName() {
        return this.nickName;
    }

    public int getRoleColor() {
        return this.roleColor;
    }

    public long getRoleId() {
        return this.roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public String getTinyId() {
        return this.tinyId;
    }

    public LiveGiftMemberInfo(String str, String str2, long j, String str3, int i) {
        this.tinyId = str;
        this.nickName = str2;
        this.roleId = j;
        this.roleName = str3;
        this.roleColor = i;
    }
}
