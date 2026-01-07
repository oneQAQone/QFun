package com.tencent.qqnt.kernel.nativeinterface;

public final class MemoryVideo {
    public boolean viewed;
    public String templateId = "";
    public String videoId = "";
    public String coverUrl = "";
    public String jumpUrl = "";
    public String title = "";
    public String subTitle = "";

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public String getJumpUrl() {
        return this.jumpUrl;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public boolean getViewed() {
        return this.viewed;
    }
}
