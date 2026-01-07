package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class BatchInfo {
    public long dayTime;
    public long linkId;
    public int linkType;
    public long originUploadNumber;
    public long uploadNumber;
    public String desc = "";
    public String showTime = "";
    public StUser user = new StUser();
    public StLBS lbs = new StLBS();
    public ArrayList<StMedia> medias = new ArrayList<>();
    public ArrayList<Entry> ext = new ArrayList<>();
    public String subTitle = "";
    public BatchInfoBanner banner = new BatchInfoBanner();
    public ArrayList<Integer> attrList = new ArrayList<>();

    public ArrayList<Integer> getAttrList() {
        return this.attrList;
    }

    public BatchInfoBanner getBanner() {
        return this.banner;
    }

    public long getDayTime() {
        return this.dayTime;
    }

    public String getDesc() {
        return this.desc;
    }

    public ArrayList<Entry> getExt() {
        return this.ext;
    }

    public StLBS getLbs() {
        return this.lbs;
    }

    public long getLinkId() {
        return this.linkId;
    }

    public int getLinkType() {
        return this.linkType;
    }

    public ArrayList<StMedia> getMedias() {
        return this.medias;
    }

    public long getOriginUploadNumber() {
        return this.originUploadNumber;
    }

    public String getShowTime() {
        return this.showTime;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public long getUploadNumber() {
        return this.uploadNumber;
    }

    public StUser getUser() {
        return this.user;
    }
}
