package com.tencent.qqnt.kernel.nativeinterface;

public final class MarkdownElementExtFlashTransferInfo {
    public long fileSize;
    public String filesetId;
    public String name;
    public Picture thnumbnail;

    public MarkdownElementExtFlashTransferInfo() {
        this.filesetId = "";
        this.name = "";
        this.thnumbnail = new Picture();
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getFilesetId() {
        return this.filesetId;
    }

    public String getName() {
        return this.name;
    }

    public Picture getThnumbnail() {
        return this.thnumbnail;
    }

    public MarkdownElementExtFlashTransferInfo(String str, String str2, long j, Picture picture) {
        this.filesetId = "";
        this.name = "";
        new Picture();
        this.filesetId = str;
        this.name = str2;
        this.fileSize = j;
        this.thnumbnail = picture;
    }
}
