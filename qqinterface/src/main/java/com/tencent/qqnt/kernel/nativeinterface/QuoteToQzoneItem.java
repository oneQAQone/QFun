package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class QuoteToQzoneItem {
    public String albumId = "";
    public ArrayList<String> lloc = new ArrayList<>();
    public long srcBatchId;

    public String getAlbumId() {
        return this.albumId;
    }

    public ArrayList<String> getLloc() {
        return this.lloc;
    }

    public long getSrcBatchId() {
        return this.srcBatchId;
    }
}
