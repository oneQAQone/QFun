package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class DeleteMediaItem {
    public String albumId = "";
    public ArrayList<String> lloc = new ArrayList<>();
    public ArrayList<String> batchid = new ArrayList<>();

    public String getAlbumId() {
        return this.albumId;
    }

    public ArrayList<String> getBatchid() {
        return this.batchid;
    }

    public ArrayList<String> getLloc() {
        return this.lloc;
    }
}
