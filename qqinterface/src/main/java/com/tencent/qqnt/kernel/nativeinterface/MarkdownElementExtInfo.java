package com.tencent.qqnt.kernel.nativeinterface;

public final class MarkdownElementExtInfo {
    public MarkdownElementExtFlashTransferInfo flashTransferInfo;

    public MarkdownElementExtInfo() {
    }

    public MarkdownElementExtFlashTransferInfo getFlashTransferInfo() {
        return this.flashTransferInfo;
    }

    public MarkdownElementExtInfo(MarkdownElementExtFlashTransferInfo markdownElementExtFlashTransferInfo) {
        this.flashTransferInfo = markdownElementExtFlashTransferInfo;
    }
}
