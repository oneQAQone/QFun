package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class QzoneMediaInfo {
    public ArrayList<StMediaWithAlbumID> medias = new ArrayList<>();
    public ArrayList<AlbumInfo> albums = new ArrayList<>();
    public ArrayList<BatchInfo> batches = new ArrayList<>();
    public QunRight right = new QunRight();

    public ArrayList<AlbumInfo> getAlbums() {
        return this.albums;
    }

    public ArrayList<BatchInfo> getBatches() {
        return this.batches;
    }

    public ArrayList<StMediaWithAlbumID> getMedias() {
        return this.medias;
    }

    public QunRight getRight() {
        return this.right;
    }
}
