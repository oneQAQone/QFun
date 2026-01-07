package com.tencent.qqnt.kernel.nativeinterface;

import java.util.HashMap;

public final class FilterMsgElement implements IKernelModel {
    public int busiType;
    public Integer fileBizId;
    public int fileFormat;
    public String fileName;
    public String filePath;
    public long fileSize;
    public String fileSubId;
    public int fileTime;
    public String fileUuid;
    public String filterId;
    public byte[] importRichMediaContext;
    public Integer invalidState;
    public String originVideoMd5;
    public boolean original;
    public Integer progress;
    public VideoCodecFormatType sourceVideoCodecFormat;
    public int storeID;
    public int subBusiType;
    public int thumbHeight;
    public String thumbMd5;
    public HashMap<Integer, String> thumbPath;
    public int thumbSize;
    public int thumbWidth;
    public Integer transferStatus;
    public int videoFrom;
    public String videoMd5;

    public FilterMsgElement() {
        this.filterId = "";
        this.filePath = "";
        this.fileName = "";
        this.videoMd5 = "";
        this.thumbMd5 = "";
        this.fileUuid = "";
        this.fileSubId = "";
        this.originVideoMd5 = "";
        this.sourceVideoCodecFormat = VideoCodecFormatType.values()[0];
    }

    public int getBusiType() {
        return this.busiType;
    }

    public Integer getFileBizId() {
        return this.fileBizId;
    }

    public int getFileFormat() {
        return this.fileFormat;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getFileSubId() {
        return this.fileSubId;
    }

    public int getFileTime() {
        return this.fileTime;
    }

    public String getFileUuid() {
        return this.fileUuid;
    }

    public String getFilterId() {
        return this.filterId;
    }

    public byte[] getImportRichMediaContext() {
        return this.importRichMediaContext;
    }

    public Integer getInvalidState() {
        return this.invalidState;
    }

    public String getOriginVideoMd5() {
        return this.originVideoMd5;
    }

    public boolean getOriginal() {
        return this.original;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public VideoCodecFormatType getSourceVideoCodecFormat() {
        return this.sourceVideoCodecFormat;
    }

    public int getStoreID() {
        return this.storeID;
    }

    public int getSubBusiType() {
        return this.subBusiType;
    }

    public int getThumbHeight() {
        return this.thumbHeight;
    }

    public String getThumbMd5() {
        return this.thumbMd5;
    }

    public HashMap<Integer, String> getThumbPath() {
        return this.thumbPath;
    }

    public int getThumbSize() {
        return this.thumbSize;
    }

    public int getThumbWidth() {
        return this.thumbWidth;
    }

    public Integer getTransferStatus() {
        return this.transferStatus;
    }

    public int getVideoFrom() {
        return this.videoFrom;
    }

    public String getVideoMd5() {
        return this.videoMd5;
    }

    public void setBusiType(int i) {
        this.busiType = i;
    }

    public void setFileBizId(Integer num) {
        this.fileBizId = num;
    }

    public void setFileFormat(int i) {
        this.fileFormat = i;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public void setFileSize(long j) {
        this.fileSize = j;
    }

    public void setFileSubId(String str) {
        this.fileSubId = str;
    }

    public void setFileTime(int i) {
        this.fileTime = i;
    }

    public void setFileUuid(String str) {
        this.fileUuid = str;
    }

    public void setFilterId(String str) {
        this.filterId = str;
    }

    public void setImportRichMediaContext(byte[] bArr) {
        this.importRichMediaContext = bArr;
    }

    public void setInvalidState(Integer num) {
        this.invalidState = num;
    }

    public void setOriginVideoMd5(String str) {
        this.originVideoMd5 = str;
    }

    public void setOriginal(boolean z) {
        this.original = z;
    }

    public void setProgress(Integer num) {
        this.progress = num;
    }

    public void setSourceVideoCodecFormat(VideoCodecFormatType videoCodecFormatType) {
        this.sourceVideoCodecFormat = videoCodecFormatType;
    }

    public void setStoreID(int i) {
        this.storeID = i;
    }

    public void setSubBusiType(int i) {
        this.subBusiType = i;
    }

    public void setThumbHeight(int i) {
        this.thumbHeight = i;
    }

    public void setThumbMd5(String str) {
        this.thumbMd5 = str;
    }

    public void setThumbPath(HashMap<Integer, String> hashMap) {
        this.thumbPath = hashMap;
    }

    public void setThumbSize(int i) {
        this.thumbSize = i;
    }

    public void setThumbWidth(int i) {
        this.thumbWidth = i;
    }

    public void setTransferStatus(Integer num) {
        this.transferStatus = num;
    }

    public void setVideoFrom(int i) {
        this.videoFrom = i;
    }

    public void setVideoMd5(String str) {
        this.videoMd5 = str;
    }

    public FilterMsgElement(String str, String str2, String str3, String str4, String str5, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, HashMap<Integer, String> hashMap, Integer num, Integer num2, Integer num3, String str6, String str7, Integer num4, String str8, byte[] bArr, VideoCodecFormatType videoCodecFormatType, int i8) {
        this.filterId = "";
        this.filePath = "";
        this.fileName = "";
        this.videoMd5 = "";
        this.thumbMd5 = "";
        this.fileUuid = "";
        this.fileSubId = "";
        this.originVideoMd5 = "";
        VideoCodecFormatType videoCodecFormatType2 = VideoCodecFormatType.values()[0];
        this.filterId = str;
        this.filePath = str2;
        this.fileName = str3;
        this.videoMd5 = str4;
        this.thumbMd5 = str5;
        this.fileTime = i;
        this.thumbSize = i2;
        this.fileFormat = i3;
        this.fileSize = j;
        this.thumbWidth = i4;
        this.thumbHeight = i5;
        this.busiType = i6;
        this.subBusiType = i7;
        this.thumbPath = hashMap;
        this.transferStatus = num;
        this.progress = num2;
        this.invalidState = num3;
        this.fileUuid = str6;
        this.fileSubId = str7;
        this.fileBizId = num4;
        this.originVideoMd5 = str8;
        this.importRichMediaContext = bArr;
        this.sourceVideoCodecFormat = videoCodecFormatType;
        this.storeID = i8;
    }
}
