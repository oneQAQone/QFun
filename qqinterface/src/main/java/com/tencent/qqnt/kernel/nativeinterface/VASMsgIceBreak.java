package com.tencent.qqnt.kernel.nativeinterface;

public final class VASMsgIceBreak {
    public Integer isIceBreakMsg;
    public Integer templateID;

    public VASMsgIceBreak() {
    }

    public Integer getIsIceBreakMsg() {
        return this.isIceBreakMsg;
    }

    public Integer getTemplateID() {
        return this.templateID;
    }

    public VASMsgIceBreak(Integer num, Integer num2) {
        this.templateID = num;
        this.isIceBreakMsg = num2;
    }
}
