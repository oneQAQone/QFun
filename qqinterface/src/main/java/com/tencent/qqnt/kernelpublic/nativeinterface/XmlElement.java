package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;
import java.util.HashMap;

public final class XmlElement implements Serializable {
    public long busiId;
    public long busiType;
    public int c2cType;
    public String content;
    public int ctrlFlag;
    public HashMap<String, String> members;
    public byte[] pbReserv;
    public Long seqId;
    long serialVersionUID;
    public int serviceType;
    public Long templId;
    public HashMap<String, String> templParam;

    public XmlElement() {
        this.serialVersionUID = 1L;
        this.content = "";
    }

    public long getBusiId() {
        return this.busiId;
    }

    public long getBusiType() {
        return this.busiType;
    }

    public int getC2cType() {
        return this.c2cType;
    }

    public String getContent() {
        return this.content;
    }

    public int getCtrlFlag() {
        return this.ctrlFlag;
    }

    public HashMap<String, String> getMembers() {
        return this.members;
    }

    public byte[] getPbReserv() {
        return this.pbReserv;
    }

    public Long getSeqId() {
        return this.seqId;
    }

    public int getServiceType() {
        return this.serviceType;
    }

    public Long getTemplId() {
        return this.templId;
    }

    public HashMap<String, String> getTemplParam() {
        return this.templParam;
    }

    public XmlElement(long j, long j2, int i, int i2, int i3, String str, Long l, Long l2, HashMap<String, String> hashMap, byte[] bArr, HashMap<String, String> hashMap2) {
        this.serialVersionUID = 1L;
        this.busiType = j;
        this.busiId = j2;
        this.c2cType = i;
        this.serviceType = i2;
        this.ctrlFlag = i3;
        this.content = str;
        this.templId = l;
        this.seqId = l2;
        this.templParam = hashMap;
        this.pbReserv = bArr;
        this.members = hashMap2;
    }
}
