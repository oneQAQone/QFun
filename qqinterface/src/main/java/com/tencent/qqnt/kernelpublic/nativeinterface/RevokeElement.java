package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class RevokeElement implements Serializable {
    public boolean isSelfOperate;
    public String operatorMemRemark;
    public String operatorNick;
    public String operatorRemark;
    public long operatorRole;
    public long operatorTinyId;
    public String operatorUid;
    public String origMsgSenderMemRemark;
    public String origMsgSenderNick;
    public String origMsgSenderRemark;
    public String origMsgSenderUid;
    long serialVersionUID;
    public String wording;

    public RevokeElement() {
        this.serialVersionUID = 1L;
        this.operatorUid = "";
        this.origMsgSenderUid = "";
        this.wording = "";
    }

    public boolean getIsSelfOperate() {
        return this.isSelfOperate;
    }

    public String getOperatorMemRemark() {
        return this.operatorMemRemark;
    }

    public String getOperatorNick() {
        return this.operatorNick;
    }

    public String getOperatorRemark() {
        return this.operatorRemark;
    }

    public long getOperatorRole() {
        return this.operatorRole;
    }

    public long getOperatorTinyId() {
        return this.operatorTinyId;
    }

    public String getOperatorUid() {
        return this.operatorUid;
    }

    public String getOrigMsgSenderMemRemark() {
        return this.origMsgSenderMemRemark;
    }

    public String getOrigMsgSenderNick() {
        return this.origMsgSenderNick;
    }

    public String getOrigMsgSenderRemark() {
        return this.origMsgSenderRemark;
    }

    public String getOrigMsgSenderUid() {
        return this.origMsgSenderUid;
    }

    public String getWording() {
        return this.wording;
    }

    public RevokeElement(long j, long j2, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, boolean z, String str9) {
        this.serialVersionUID = 1L;
        this.operatorTinyId = j;
        this.operatorRole = j2;
        this.operatorUid = str;
        this.operatorNick = str2;
        this.operatorRemark = str3;
        this.operatorMemRemark = str4;
        this.origMsgSenderUid = str5;
        this.origMsgSenderNick = str6;
        this.origMsgSenderRemark = str7;
        this.origMsgSenderMemRemark = str8;
        this.isSelfOperate = z;
        this.wording = str9;
    }
}
