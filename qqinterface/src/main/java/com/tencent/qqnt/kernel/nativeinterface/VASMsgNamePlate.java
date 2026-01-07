package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class VASMsgNamePlate {
    public Integer aioBadgeShowSwitch;
    public ArrayList<Integer> carouselNamePlateIds;
    public Integer extendNamePlateId;
    public Integer gameNamePlateId;
    public Integer grayNamePlate;
    public Integer msgBigClubFlag;
    public Integer msgBigClubLevel;
    public Integer msgVipLevel;
    public Integer msgVipType;
    public Integer namePlateId;
    public Integer namePlateType;
    public Integer vipStarFlag;

    public VASMsgNamePlate() {
        this.carouselNamePlateIds = new ArrayList<>();
    }

    public Integer getAioBadgeShowSwitch() {
        return this.aioBadgeShowSwitch;
    }

    public ArrayList<Integer> getCarouselNamePlateIds() {
        return this.carouselNamePlateIds;
    }

    public Integer getExtendNamePlateId() {
        return this.extendNamePlateId;
    }

    public Integer getGameNamePlateId() {
        return this.gameNamePlateId;
    }

    public Integer getGrayNamePlate() {
        return this.grayNamePlate;
    }

    public Integer getMsgBigClubFlag() {
        return this.msgBigClubFlag;
    }

    public Integer getMsgBigClubLevel() {
        return this.msgBigClubLevel;
    }

    public Integer getMsgVipLevel() {
        return this.msgVipLevel;
    }

    public Integer getMsgVipType() {
        return this.msgVipType;
    }

    public Integer getNamePlateId() {
        return this.namePlateId;
    }

    public Integer getNamePlateType() {
        return this.namePlateType;
    }

    public Integer getVipStarFlag() {
        return this.vipStarFlag;
    }

    public VASMsgNamePlate(Integer num, Integer num2, Integer num3, Integer num4, Integer num5, Integer num6, Integer num7, Integer num8, ArrayList<Integer> arrayList, Integer num9, Integer num10, Integer num11) {
        this.msgVipType = num;
        this.msgVipLevel = num2;
        this.msgBigClubFlag = num3;
        this.msgBigClubLevel = num4;
        this.grayNamePlate = num5;
        this.namePlateType = num6;
        this.vipStarFlag = num7;
        this.namePlateId = num8;
        this.carouselNamePlateIds = arrayList;
        this.extendNamePlateId = num9;
        this.gameNamePlateId = num10;
        this.aioBadgeShowSwitch = num11;
    }
}
