package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class SysEmoji {
    public int aniStickerId;
    public int aniStickerPackId;
    public int animationHeigh;
    public int animationWidth;
    public long endTime;
    public int interactStickerId;
    public int interactStickerPackId;
    public boolean isHide;
    public boolean needRefreshPanel;
    public int qcid;
    public long startTime;
    public String emojiId = "";
    public String describe = "";
    public String qzoneCode = "";
    public BaseEmojiType emojiType = BaseEmojiType.values()[0];
    public DownloadBaseEmojiInfo downloadInfo = new DownloadBaseEmojiInfo();
    public ArrayList<String> associateWords = new ArrayList<>();

    public int getAniStickerId() {
        return this.aniStickerId;
    }

    public int getAniStickerPackId() {
        return this.aniStickerPackId;
    }

    public int getAnimationHeigh() {
        return this.animationHeigh;
    }

    public int getAnimationWidth() {
        return this.animationWidth;
    }

    public ArrayList<String> getAssociateWords() {
        return this.associateWords;
    }

    public String getDescribe() {
        return this.describe;
    }

    public DownloadBaseEmojiInfo getDownloadInfo() {
        return this.downloadInfo;
    }

    public String getEmojiId() {
        return this.emojiId;
    }

    public BaseEmojiType getEmojiType() {
        return this.emojiType;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getInteractStickerId() {
        return this.interactStickerId;
    }

    public int getInteractStickerPackId() {
        return this.interactStickerPackId;
    }

    public boolean getIsHide() {
        return this.isHide;
    }

    public boolean getNeedRefreshPanel() {
        return this.needRefreshPanel;
    }

    public int getQcid() {
        return this.qcid;
    }

    public String getQzoneCode() {
        return this.qzoneCode;
    }

    public long getStartTime() {
        return this.startTime;
    }
}
