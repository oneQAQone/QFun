package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class WalletElement {
    public String authkey;
    public String billNo;
    public int channelId;
    public int confType;
    public int envelopeId;
    public int grabState;
    public long grabbedAmount;
    public ArrayList<Long> grapUin;
    public int msgFrom;
    public int msgPriority;
    public int msgType;
    public String name;
    public byte[] pbReserve;
    public byte[] pcBody;
    public WalletAio receiver;
    public int redChannel;
    public int redType;
    public int resend;
    public long sendUin;
    public WalletAio sender;
    public int sessiontype;
    public byte[] stringIndex;
    public int templateId;

    public WalletElement() {
        this.sender = new WalletAio();
        this.receiver = new WalletAio();
        this.billNo = "";
        this.authkey = "";
        this.name = "";
        this.pcBody = new byte[0];
        this.stringIndex = new byte[0];
        this.grapUin = new ArrayList<>();
        this.pbReserve = new byte[0];
    }

    public String getAuthkey() {
        return this.authkey;
    }

    public String getBillNo() {
        return this.billNo;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public int getConfType() {
        return this.confType;
    }

    public int getEnvelopeId() {
        return this.envelopeId;
    }

    public int getGrabState() {
        return this.grabState;
    }

    public long getGrabbedAmount() {
        return this.grabbedAmount;
    }

    public ArrayList<Long> getGrapUin() {
        return this.grapUin;
    }

    public int getMsgFrom() {
        return this.msgFrom;
    }

    public int getMsgPriority() {
        return this.msgPriority;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public String getName() {
        return this.name;
    }

    public byte[] getPbReserve() {
        return this.pbReserve;
    }

    public byte[] getPcBody() {
        return this.pcBody;
    }

    public WalletAio getReceiver() {
        return this.receiver;
    }

    public int getRedChannel() {
        return this.redChannel;
    }

    public int getRedType() {
        return this.redType;
    }

    public int getResend() {
        return this.resend;
    }

    public long getSendUin() {
        return this.sendUin;
    }

    public WalletAio getSender() {
        return this.sender;
    }

    public int getSessiontype() {
        return this.sessiontype;
    }

    public byte[] getStringIndex() {
        return this.stringIndex;
    }

    public int getTemplateId() {
        return this.templateId;
    }

    public WalletElement(long j, WalletAio walletAio, WalletAio walletAio2, int i, int i2, int i3, int i4, int i5, String str, String str2, int i6, int i7, int i8, String str3, int i9, int i10, byte[] bArr, byte[] bArr2, int i11, ArrayList<Long> arrayList, byte[] bArr3, int i12, long j2) {
        this.sender = new WalletAio();
        this.receiver = new WalletAio();
        this.billNo = "";
        this.authkey = "";
        this.name = "";
        this.pcBody = new byte[0];
        this.stringIndex = new byte[0];
        this.sendUin = j;
        this.sender = walletAio;
        this.receiver = walletAio2;
        this.channelId = i;
        this.templateId = i2;
        this.resend = i3;
        this.msgPriority = i4;
        this.redType = i5;
        this.billNo = str;
        this.authkey = str2;
        this.sessiontype = i6;
        this.msgType = i7;
        this.envelopeId = i8;
        this.name = str3;
        this.confType = i9;
        this.msgFrom = i10;
        this.pcBody = bArr;
        this.stringIndex = bArr2;
        this.redChannel = i11;
        this.grapUin = arrayList;
        this.pbReserve = bArr3;
        this.grabState = i12;
        this.grabbedAmount = j2;
    }
}
