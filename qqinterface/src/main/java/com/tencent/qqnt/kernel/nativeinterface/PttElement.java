package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class PttElement implements IKernelModel {
    public int autoConvertText;
    public boolean canConvert2Text;
    public int duration;
    public Integer fileBizId;
    public Integer fileId;
    public String fileName;
    public String filePath;
    public long fileSize;
    public String fileSubId;
    public String fileUuid;
    public int formatType;
    public byte[] importRichMediaContext;
    public Integer invalidState;
    public Boolean isInApplicationDataPath;
    public String md5HexStr;
    public OtherBusinessInfo otherBusinessInfo;
    public Integer playState;
    public Integer progress;
    public int storeID;
    public String text;
    public Integer transferStatus;
    public Integer translateStatus;
    public int voiceChangeType;
    public int voiceType;
    public ArrayList<Byte> waveAmplitudes;

    public PttElement() {
        this.fileName = "";
        this.filePath = "";
        this.md5HexStr = "";
        this.waveAmplitudes = new ArrayList<>();
        this.fileSubId = "";
        this.otherBusinessInfo = new OtherBusinessInfo();
    }

    public int getAutoConvertText() {
        return this.autoConvertText;
    }

    public boolean getCanConvert2Text() {
        return this.canConvert2Text;
    }

    public int getDuration() {
        return this.duration;
    }

    public Integer getFileBizId() {
        return this.fileBizId;
    }

    public Integer getFileId() {
        return this.fileId;
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

    public String getFileUuid() {
        return this.fileUuid;
    }

    public int getFormatType() {
        return this.formatType;
    }

    public byte[] getImportRichMediaContext() {
        return this.importRichMediaContext;
    }

    public Integer getInvalidState() {
        return this.invalidState;
    }

    public Boolean getIsInApplicationDataPath() {
        return this.isInApplicationDataPath;
    }

    public String getMd5HexStr() {
        return this.md5HexStr;
    }

    public OtherBusinessInfo getOtherBusinessInfo() {
        return this.otherBusinessInfo;
    }

    public Integer getPlayState() {
        return this.playState;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public int getStoreID() {
        return this.storeID;
    }

    public String getText() {
        return this.text;
    }

    public Integer getTransferStatus() {
        return this.transferStatus;
    }

    public Integer getTranslateStatus() {
        return this.translateStatus;
    }

    public int getVoiceChangeType() {
        return this.voiceChangeType;
    }

    public int getVoiceType() {
        return this.voiceType;
    }

    public ArrayList<Byte> getWaveAmplitudes() {
        return this.waveAmplitudes;
    }

    public void setAutoConvertText(int i) {
        this.autoConvertText = i;
    }

    public void setCanConvert2Text(boolean z) {
        this.canConvert2Text = z;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public void setFileBizId(Integer num) {
        this.fileBizId = num;
    }

    public void setFileId(Integer num) {
        this.fileId = num;
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

    public void setFileUuid(String str) {
        this.fileUuid = str;
    }

    public void setFormatType(int i) {
        this.formatType = i;
    }

    public void setImportRichMediaContext(byte[] bArr) {
        this.importRichMediaContext = bArr;
    }

    public void setInvalidState(Integer num) {
        this.invalidState = num;
    }

    public void setIsInApplicationDataPath(Boolean bool) {
        this.isInApplicationDataPath = bool;
    }

    public void setMd5HexStr(String str) {
        this.md5HexStr = str;
    }

    public void setOtherBusinessInfo(OtherBusinessInfo otherBusinessInfo) {
        this.otherBusinessInfo = otherBusinessInfo;
    }

    public void setPlayState(Integer num) {
        this.playState = num;
    }

    public void setProgress(Integer num) {
        this.progress = num;
    }

    public void setStoreID(int i) {
        this.storeID = i;
    }

    public void setText(String str) {
        this.text = str;
    }

    public void setTransferStatus(Integer num) {
        this.transferStatus = num;
    }

    public void setTranslateStatus(Integer num) {
        this.translateStatus = num;
    }

    public void setVoiceChangeType(int i) {
        this.voiceChangeType = i;
    }

    public void setVoiceType(int i) {
        this.voiceType = i;
    }

    public void setWaveAmplitudes(ArrayList<Byte> arrayList) {
        this.waveAmplitudes = arrayList;
    }

    public PttElement(String str, String str2, String str3, long j, int i, int i2, int i3, int i4, int i5, boolean z, Integer num, String str4, String str5, Integer num2, Integer num3, Integer num4, Integer num5, ArrayList<Byte> arrayList, Integer num6, String str6, Integer num7, byte[] bArr, int i6) {
        this.fileName = "";
        this.filePath = "";
        this.md5HexStr = "";
        this.waveAmplitudes = new ArrayList<>();
        this.fileSubId = "";
        this.otherBusinessInfo = new OtherBusinessInfo();
        this.fileName = str;
        this.filePath = str2;
        this.md5HexStr = str3;
        this.fileSize = j;
        this.duration = i;
        this.formatType = i2;
        this.voiceType = i3;
        this.autoConvertText = i4;
        this.voiceChangeType = i5;
        this.canConvert2Text = z;
        this.fileId = num;
        this.fileUuid = str4;
        this.text = str5;
        this.translateStatus = num2;
        this.transferStatus = num3;
        this.progress = num4;
        this.playState = num5;
        this.waveAmplitudes = arrayList;
        this.invalidState = num6;
        this.fileSubId = str6;
        this.fileBizId = num7;
        this.importRichMediaContext = bArr;
        this.storeID = i6;
    }
}
