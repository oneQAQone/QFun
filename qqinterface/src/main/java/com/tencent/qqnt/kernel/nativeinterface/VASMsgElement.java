package com.tencent.qqnt.kernel.nativeinterface;

public final class VASMsgElement {
    public VASMsgAvatarPendant avatarPendantInfo;
    public VASMsgBubble bubbleInfo;
    public VASMsgIceBreak iceBreakInfo;
    public VASMsgNamePlate msgNamePlateInfo;
    public VASMsgFont vasFont;

    public VASMsgElement() {
        this.msgNamePlateInfo = new VASMsgNamePlate();
        this.bubbleInfo = new VASMsgBubble();
        this.avatarPendantInfo = new VASMsgAvatarPendant();
        this.vasFont = new VASMsgFont();
        this.iceBreakInfo = new VASMsgIceBreak();
    }

    public VASMsgAvatarPendant getAvatarPendantInfo() {
        return this.avatarPendantInfo;
    }

    public VASMsgBubble getBubbleInfo() {
        return this.bubbleInfo;
    }

    public VASMsgIceBreak getIceBreakInfo() {
        return this.iceBreakInfo;
    }

    public VASMsgNamePlate getMsgNamePlateInfo() {
        return this.msgNamePlateInfo;
    }

    public VASMsgFont getVasFont() {
        return this.vasFont;
    }

    public VASMsgElement(VASMsgNamePlate vASMsgNamePlate, VASMsgBubble vASMsgBubble, VASMsgAvatarPendant vASMsgAvatarPendant, VASMsgFont vASMsgFont, VASMsgIceBreak vASMsgIceBreak) {
        this.msgNamePlateInfo = new VASMsgNamePlate();
        this.bubbleInfo = new VASMsgBubble();
        this.avatarPendantInfo = new VASMsgAvatarPendant();
        this.vasFont = new VASMsgFont();
        this.msgNamePlateInfo = vASMsgNamePlate;
        this.bubbleInfo = vASMsgBubble;
        this.avatarPendantInfo = vASMsgAvatarPendant;
        this.vasFont = vASMsgFont;
        this.iceBreakInfo = vASMsgIceBreak;
    }
}
