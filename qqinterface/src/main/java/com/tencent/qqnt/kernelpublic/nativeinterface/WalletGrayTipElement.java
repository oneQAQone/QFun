package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class WalletGrayTipElement implements Serializable {
    public byte[] authkey;
    public byte[] broadcastRichContent;
    public int hideFlag;
    public int icon;
    public byte[] idiom;
    public byte[] idiomAlpha;
    public int idiomSeq;
    public String jumpUrl;
    public int luckyFlag;
    public long luckyUin;
    public int msgType;
    public byte[] pcBody;
    public byte[] poemRule;
    public int random;
    public byte[] receiverRichContent;
    public long receiverUin;
    public byte[] senderRichContent;
    public long senderUin;
    long serialVersionUID;
    public int showLastest;
    public int subchannel;
    public int time;

    public WalletGrayTipElement() {
        this.serialVersionUID = 1L;
        this.senderRichContent = new byte[0];
        this.receiverRichContent = new byte[0];
        this.authkey = new byte[0];
        this.pcBody = new byte[0];
        this.broadcastRichContent = new byte[0];
        this.idiom = new byte[0];
        this.idiomAlpha = new byte[0];
        this.jumpUrl = "";
        this.poemRule = new byte[0];
    }

    public byte[] getAuthkey() {
        return this.authkey;
    }

    public byte[] getBroadcastRichContent() {
        return this.broadcastRichContent;
    }

    public int getHideFlag() {
        return this.hideFlag;
    }

    public int getIcon() {
        return this.icon;
    }

    public byte[] getIdiom() {
        return this.idiom;
    }

    public byte[] getIdiomAlpha() {
        return this.idiomAlpha;
    }

    public int getIdiomSeq() {
        return this.idiomSeq;
    }

    public String getJumpUrl() {
        return this.jumpUrl;
    }

    public int getLuckyFlag() {
        return this.luckyFlag;
    }

    public long getLuckyUin() {
        return this.luckyUin;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public byte[] getPcBody() {
        return this.pcBody;
    }

    public byte[] getPoemRule() {
        return this.poemRule;
    }

    public int getRandom() {
        return this.random;
    }

    public byte[] getReceiverRichContent() {
        return this.receiverRichContent;
    }

    public long getReceiverUin() {
        return this.receiverUin;
    }

    public byte[] getSenderRichContent() {
        return this.senderRichContent;
    }

    public long getSenderUin() {
        return this.senderUin;
    }

    public int getShowLastest() {
        return this.showLastest;
    }

    public int getSubchannel() {
        return this.subchannel;
    }

    public int getTime() {
        return this.time;
    }

    public WalletGrayTipElement(int i, long j, long j2, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2, int i3, int i4, byte[] bArr4, int i5, long j3, int i6, int i7, byte[] bArr5, byte[] bArr6, int i8, byte[] bArr7, String str, int i9, byte[] bArr8) {
        this.serialVersionUID = 1L;
        this.showLastest = i;
        this.senderUin = j;
        this.receiverUin = j2;
        this.senderRichContent = bArr;
        this.receiverRichContent = bArr2;
        this.authkey = bArr3;
        this.msgType = i2;
        this.luckyFlag = i3;
        this.hideFlag = i4;
        this.pcBody = bArr4;
        this.icon = i5;
        this.luckyUin = j3;
        this.time = i6;
        this.random = i7;
        this.broadcastRichContent = bArr5;
        this.idiom = bArr6;
        this.idiomSeq = i8;
        this.idiomAlpha = bArr7;
        this.jumpUrl = str;
        this.subchannel = i9;
        this.poemRule = bArr8;
    }
}
