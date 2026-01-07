package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class BannerInfo {
    public String bannerText = "";
    public ArrayList<FacePerson> facePerson = new ArrayList<>();
    public boolean isDisplay;

    public String getBannerText() {
        return this.bannerText;
    }

    public ArrayList<FacePerson> getFacePerson() {
        return this.facePerson;
    }

    public boolean getIsDisplay() {
        return this.isDisplay;
    }
}
