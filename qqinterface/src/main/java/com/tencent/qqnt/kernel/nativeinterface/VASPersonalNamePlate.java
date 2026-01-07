package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class VASPersonalNamePlate {
    public Integer aioBadgeShowSwitch;
    public ArrayList<Integer> carouselNamePlateIds;
    public ArrayList<Integer> diyNamePlateContentIds;
    public Integer diyNamePlateItemId;
    public Integer extendNamePlateId;
    public Integer gameNamePlateId;
    public Integer isGray;
    public Integer namePlateId;
    public Integer vipLevel;
    public Integer vipStarFlag;
    public Integer vipType;

    public VASPersonalNamePlate() {
        this.carouselNamePlateIds = new ArrayList<>();
        this.diyNamePlateContentIds = new ArrayList<>();
    }

    public Integer getAioBadgeShowSwitch() {
        return this.aioBadgeShowSwitch;
    }

    public ArrayList<Integer> getCarouselNamePlateIds() {
        return this.carouselNamePlateIds;
    }

    public ArrayList<Integer> getDiyNamePlateContentIds() {
        return this.diyNamePlateContentIds;
    }

    public Integer getDiyNamePlateItemId() {
        return this.diyNamePlateItemId;
    }

    public Integer getExtendNamePlateId() {
        return this.extendNamePlateId;
    }

    public Integer getGameNamePlateId() {
        return this.gameNamePlateId;
    }

    public Integer getIsGray() {
        return this.isGray;
    }

    public Integer getNamePlateId() {
        return this.namePlateId;
    }

    public Integer getVipLevel() {
        return this.vipLevel;
    }

    public Integer getVipStarFlag() {
        return this.vipStarFlag;
    }

    public Integer getVipType() {
        return this.vipType;
    }

    public VASPersonalNamePlate(Integer num, Integer num2, Integer num3, Integer num4, ArrayList<Integer> arrayList, Integer num5, ArrayList<Integer> arrayList2, Integer num6, Integer num7, Integer num8, Integer num9) {
        this.carouselNamePlateIds = new ArrayList<>();
        this.isGray = num;
        this.vipType = num2;
        this.vipLevel = num3;
        this.namePlateId = num4;
        this.carouselNamePlateIds = arrayList;
        this.diyNamePlateItemId = num5;
        this.diyNamePlateContentIds = arrayList2;
        this.extendNamePlateId = num6;
        this.gameNamePlateId = num7;
        this.vipStarFlag = num8;
        this.aioBadgeShowSwitch = num9;
    }
}
