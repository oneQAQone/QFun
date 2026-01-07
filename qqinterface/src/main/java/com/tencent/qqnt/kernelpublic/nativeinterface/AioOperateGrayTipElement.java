package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class AioOperateGrayTipElement implements Serializable {
    public String fromGrpCodeOfTmpChat;
    public int operateType;
    public String peerUid;
    long serialVersionUID;

    public AioOperateGrayTipElement() {
        this.serialVersionUID = 1L;
        this.peerUid = "";
        this.fromGrpCodeOfTmpChat = "";
    }

    public String getFromGrpCodeOfTmpChat() {
        return this.fromGrpCodeOfTmpChat;
    }

    public int getOperateType() {
        return this.operateType;
    }

    public String getPeerUid() {
        return this.peerUid;
    }

    public AioOperateGrayTipElement(int i, String str, String str2) {
        this.serialVersionUID = 1L;
        this.operateType = i;
        this.peerUid = str;
        this.fromGrpCodeOfTmpChat = str2;
    }
}
