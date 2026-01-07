package com.tencent.qqnt.kernel.nativeinterface;

public final class TofuActionButton {
    public int emojiId;
    public int emojiType;
    public String text = "";

    public int getEmojiId() {
        return this.emojiId;
    }

    public int getEmojiType() {
        return this.emojiType;
    }

    public String getText() {
        return this.text;
    }
}
