package com.tencent.qqnt.kernel.nativeinterface;

public final class CommonTabEmojiInfo {
    public int bottomEmojitabType;
    public int epId;
    public int expireTime;
    public int flags;
    public boolean isHide;
    public String tabName = "";
    public int wordingId;

    public int getBottomEmojitabType() {
        return this.bottomEmojitabType;
    }

    public int getEpId() {
        return this.epId;
    }

    public int getExpireTime() {
        return this.expireTime;
    }

    public int getFlags() {
        return this.flags;
    }

    public boolean getIsHide() {
        return this.isHide;
    }

    public String getTabName() {
        return this.tabName;
    }

    public int getWordingId() {
        return this.wordingId;
    }
}
