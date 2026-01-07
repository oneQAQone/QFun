package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StFeedCellMedia {
    public int albumUploadNum;
    public long batchId;
    public int uploadNum;
    public ArrayList<StMedia> mediaItems = new ArrayList<>();
    public String albumName = "";
    public String albumId = "";
    public StImage albumCover = new StImage();

    public StImage getAlbumCover() {
        return this.albumCover;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public int getAlbumUploadNum() {
        return this.albumUploadNum;
    }

    public long getBatchId() {
        return this.batchId;
    }

    public ArrayList<StMedia> getMediaItems() {
        return this.mediaItems;
    }

    public int getUploadNum() {
        return this.uploadNum;
    }

    public void setAlbumCover(StImage stImage) {
        this.albumCover = stImage;
    }

    public void setAlbumId(String str) {
        this.albumId = str;
    }

    public void setAlbumName(String str) {
        this.albumName = str;
    }

    public void setAlbumUploadNum(int i) {
        this.albumUploadNum = i;
    }

    public void setBatchId(long j) {
        this.batchId = j;
    }

    public void setMediaItems(ArrayList<StMedia> arrayList) {
        this.mediaItems = arrayList;
    }

    public void setUploadNum(int i) {
        this.uploadNum = i;
    }
}
