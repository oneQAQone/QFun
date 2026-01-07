package com.tencent.qqnt.kernel.nativeinterface;

public final class VASMsgBubble {
    public Integer bubbleDiyTextId;
    public Integer bubbleId;
    public Integer canConvertToText;
    public Integer subBubbleId;

    public VASMsgBubble() {
    }

    public Integer getBubbleDiyTextId() {
        return this.bubbleDiyTextId;
    }

    public Integer getBubbleId() {
        return this.bubbleId;
    }

    public Integer getCanConvertToText() {
        return this.canConvertToText;
    }

    public Integer getSubBubbleId() {
        return this.subBubbleId;
    }

    public VASMsgBubble(Integer num, Integer num2, Integer num3, Integer num4) {
        this.bubbleId = num;
        this.bubbleDiyTextId = num2;
        this.subBubbleId = num3;
        this.canConvertToText = num4;
    }
}
