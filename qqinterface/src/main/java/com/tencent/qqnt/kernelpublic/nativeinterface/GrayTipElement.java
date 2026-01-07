package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class GrayTipElement implements Serializable {
    public AioOperateGrayTipElement aioOpGrayTipElement;
    public BlockGrayTipElement blockGrayTipElement;
    public BuddyGrayElement buddyElement;
    public EmojiReplyElement emojiReplyElement;
    public EssenceElement essenceElement;
    public FeedMsgElement feedMsgElement;
    public FileReceiptElement fileReceiptElement;
    public GroupGrayElement groupElement;
    public JsonGrayElement jsonGrayTipElement;
    public LocalGrayTipElement localGrayTipElement;
    public ProclamationElement proclamationElement;
    public RevokeElement revokeElement;
    long serialVersionUID = 1;
    public int subElementType;
    public WalletGrayTipElement walletGrayTipElement;
    public XmlElement xmlElement;

    public GrayTipElement() {
    }

    public AioOperateGrayTipElement getAioOpGrayTipElement() {
        return this.aioOpGrayTipElement;
    }

    public BlockGrayTipElement getBlockGrayTipElement() {
        return this.blockGrayTipElement;
    }

    public BuddyGrayElement getBuddyElement() {
        return this.buddyElement;
    }

    public EmojiReplyElement getEmojiReplyElement() {
        return this.emojiReplyElement;
    }

    public EssenceElement getEssenceElement() {
        return this.essenceElement;
    }

    public FeedMsgElement getFeedMsgElement() {
        return this.feedMsgElement;
    }

    public FileReceiptElement getFileReceiptElement() {
        return this.fileReceiptElement;
    }

    public GroupGrayElement getGroupElement() {
        return this.groupElement;
    }

    public JsonGrayElement getJsonGrayTipElement() {
        return this.jsonGrayTipElement;
    }

    public LocalGrayTipElement getLocalGrayTipElement() {
        return this.localGrayTipElement;
    }

    public ProclamationElement getProclamationElement() {
        return this.proclamationElement;
    }

    public RevokeElement getRevokeElement() {
        return this.revokeElement;
    }

    public int getSubElementType() {
        return this.subElementType;
    }

    public WalletGrayTipElement getWalletGrayTipElement() {
        return this.walletGrayTipElement;
    }

    public XmlElement getXmlElement() {
        return this.xmlElement;
    }

    public GrayTipElement(int i, RevokeElement revokeElement, ProclamationElement proclamationElement, EmojiReplyElement emojiReplyElement, GroupGrayElement groupGrayElement, BuddyGrayElement buddyGrayElement, FeedMsgElement feedMsgElement, EssenceElement essenceElement, XmlElement xmlElement, FileReceiptElement fileReceiptElement, LocalGrayTipElement localGrayTipElement, BlockGrayTipElement blockGrayTipElement, AioOperateGrayTipElement aioOperateGrayTipElement, JsonGrayElement jsonGrayElement, WalletGrayTipElement walletGrayTipElement) {
        this.subElementType = i;
        this.revokeElement = revokeElement;
        this.proclamationElement = proclamationElement;
        this.emojiReplyElement = emojiReplyElement;
        this.groupElement = groupGrayElement;
        this.buddyElement = buddyGrayElement;
        this.feedMsgElement = feedMsgElement;
        this.essenceElement = essenceElement;
        this.xmlElement = xmlElement;
        this.fileReceiptElement = fileReceiptElement;
        this.localGrayTipElement = localGrayTipElement;
        this.blockGrayTipElement = blockGrayTipElement;
        this.aioOpGrayTipElement = aioOperateGrayTipElement;
        this.jsonGrayTipElement = jsonGrayElement;
        this.walletGrayTipElement = walletGrayTipElement;
    }
}
