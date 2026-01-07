package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class BlockGrayTipElement implements Serializable {
    public boolean block;
    public boolean isBuddy;
    public String peerUid;
    long serialVersionUID;

    public BlockGrayTipElement() {
        this.serialVersionUID = 1L;
        this.peerUid = "";
    }

    public boolean getBlock() {
        return this.block;
    }

    public boolean getIsBuddy() {
        return this.isBuddy;
    }

    public String getPeerUid() {
        return this.peerUid;
    }

    public BlockGrayTipElement(String str, boolean z, boolean z2) {
        this.serialVersionUID = 1L;
        this.peerUid = str;
        this.block = z;
        this.isBuddy = z2;
    }
}
