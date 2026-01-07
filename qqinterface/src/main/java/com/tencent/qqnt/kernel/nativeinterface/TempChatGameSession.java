package com.tencent.qqnt.kernel.nativeinterface;

public final class TempChatGameSession {
    public long gameAppId;
    public String nickname;
    public String peerOpenId;
    public String peerRoleId;
    public long peerTinyId;
    public Integer pushWindowFlag;
    public Integer redPointSwitch;
    public Integer relationType;
    public Integer sayHiType;
    public Integer seekingPartner;
    public String selfOpenId;
    public String selfRoleId;
    public long selfTinyId;
    public Integer source;

    public TempChatGameSession() {
        this.nickname = "";
        this.selfRoleId = "";
        this.selfOpenId = "";
        this.peerRoleId = "";
        this.peerOpenId = "";
    }

    public long getGameAppId() {
        return this.gameAppId;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getPeerOpenId() {
        return this.peerOpenId;
    }

    public String getPeerRoleId() {
        return this.peerRoleId;
    }

    public long getPeerTinyId() {
        return this.peerTinyId;
    }

    public Integer getPushWindowFlag() {
        return this.pushWindowFlag;
    }

    public Integer getRedPointSwitch() {
        return this.redPointSwitch;
    }

    public Integer getRelationType() {
        return this.relationType;
    }

    public Integer getSayHiType() {
        return this.sayHiType;
    }

    public Integer getSeekingPartner() {
        return this.seekingPartner;
    }

    public String getSelfOpenId() {
        return this.selfOpenId;
    }

    public String getSelfRoleId() {
        return this.selfRoleId;
    }

    public long getSelfTinyId() {
        return this.selfTinyId;
    }

    public Integer getSource() {
        return this.source;
    }

    public TempChatGameSession(long j, String str, long j2, String str2, String str3, long j3, String str4, String str5, Integer num, Integer num2, Integer num3, Integer num4) {
        this.gameAppId = j;
        this.nickname = str;
        this.selfTinyId = j2;
        this.selfRoleId = str2;
        this.selfOpenId = str3;
        this.peerTinyId = j3;
        this.peerRoleId = str4;
        this.peerOpenId = str5;
        this.pushWindowFlag = num;
        this.sayHiType = num2;
        this.redPointSwitch = num3;
        this.seekingPartner = num4;
    }
}
