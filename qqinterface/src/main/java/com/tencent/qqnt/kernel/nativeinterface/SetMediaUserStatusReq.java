package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class SetMediaUserStatusReq {
    public long groupCode;
    public boolean isSetDownloadMask;
    public boolean isSetQuoteMask;
    public ArrayList<AlbumMetaMediaInfo> mediaInfos = new ArrayList<>();
    public int seq;

    public long getGroupCode() {
        return this.groupCode;
    }

    public boolean getIsSetDownloadMask() {
        return this.isSetDownloadMask;
    }

    public boolean getIsSetQuoteMask() {
        return this.isSetQuoteMask;
    }

    public ArrayList<AlbumMetaMediaInfo> getMediaInfos() {
        return this.mediaInfos;
    }

    public int getSeq() {
        return this.seq;
    }
}
