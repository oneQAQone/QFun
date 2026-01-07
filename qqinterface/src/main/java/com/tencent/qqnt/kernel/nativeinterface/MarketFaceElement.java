package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class MarketFaceElement {
    public ArrayList<MarketFaceSupportSize> apngSupportSize;
    public String backColor;
    public String dynamicFacePath;
    public String emojiId;
    public int emojiPackageId;
    public Integer emojiType;
    public Integer endTime;
    public int faceInfo;
    public String faceName;
    public Integer hasIpProduct;
    public int imageHeight;
    public int imageWidth;
    public int itemType;
    public String key;
    public int mediaType;
    public byte[] mobileParam;
    public byte[] param;
    public String sourceJumpUrl;
    public String sourceName;
    public Integer sourceType;
    public String sourceTypeName;
    public Integer startTime;
    public String staticFacePath;
    public int subType;
    public ArrayList<MarketFaceSupportSize> supportSize;
    public ArrayList<Integer> voiceItemHeightArr;
    public String volumeColor;

    public MarketFaceElement() {
    }

    public ArrayList<MarketFaceSupportSize> getApngSupportSize() {
        return this.apngSupportSize;
    }

    public String getBackColor() {
        return this.backColor;
    }

    public String getDynamicFacePath() {
        return this.dynamicFacePath;
    }

    public String getEmojiId() {
        return this.emojiId;
    }

    public int getEmojiPackageId() {
        return this.emojiPackageId;
    }

    public Integer getEmojiType() {
        return this.emojiType;
    }

    public Integer getEndTime() {
        return this.endTime;
    }

    public int getFaceInfo() {
        return this.faceInfo;
    }

    public String getFaceName() {
        return this.faceName;
    }

    public Integer getHasIpProduct() {
        return this.hasIpProduct;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public int getItemType() {
        return this.itemType;
    }

    public String getKey() {
        return this.key;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public byte[] getMobileParam() {
        return this.mobileParam;
    }

    public byte[] getParam() {
        return this.param;
    }

    public String getSourceJumpUrl() {
        return this.sourceJumpUrl;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public Integer getSourceType() {
        return this.sourceType;
    }

    public String getSourceTypeName() {
        return this.sourceTypeName;
    }

    public Integer getStartTime() {
        return this.startTime;
    }

    public String getStaticFacePath() {
        return this.staticFacePath;
    }

    public int getSubType() {
        return this.subType;
    }

    public ArrayList<MarketFaceSupportSize> getSupportSize() {
        return this.supportSize;
    }

    public ArrayList<Integer> getVoiceItemHeightArr() {
        return this.voiceItemHeightArr;
    }

    public String getVolumeColor() {
        return this.volumeColor;
    }

    public MarketFaceElement(int i, int i2, int i3, int i4, int i5, int i6, int i7, String str, String str2, String str3, byte[] bArr, byte[] bArr2, Integer num, Integer num2, Integer num3, Integer num4, Integer num5, ArrayList<Integer> arrayList, String str4, String str5, String str6, String str7, String str8, String str9, String str10, ArrayList<MarketFaceSupportSize> arrayList2, ArrayList<MarketFaceSupportSize> arrayList3) {
        this.itemType = i;
        this.faceInfo = i2;
        this.emojiPackageId = i3;
        this.subType = i4;
        this.mediaType = i5;
        this.imageWidth = i6;
        this.imageHeight = i7;
        this.faceName = str;
        this.emojiId = str2;
        this.key = str3;
        this.param = bArr;
        this.mobileParam = bArr2;
        this.sourceType = num;
        this.startTime = num2;
        this.endTime = num3;
        this.emojiType = num4;
        this.hasIpProduct = num5;
        this.voiceItemHeightArr = arrayList;
        this.sourceName = str4;
        this.sourceJumpUrl = str5;
        this.sourceTypeName = str6;
        this.backColor = str7;
        this.volumeColor = str8;
        this.staticFacePath = str9;
        this.dynamicFacePath = str10;
        this.supportSize = arrayList2;
        this.apngSupportSize = arrayList3;
    }
}
