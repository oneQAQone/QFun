package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class ShareAlbumMeta {
    public boolean autoJoin;
    public int shareNums;
    public StUser owner = new StUser();
    public ArrayList<ClientAttr> shareattrs = new ArrayList<>();

    public boolean getAutoJoin() {
        return this.autoJoin;
    }

    public StUser getOwner() {
        return this.owner;
    }

    public int getShareNums() {
        return this.shareNums;
    }

    public ArrayList<ClientAttr> getShareattrs() {
        return this.shareattrs;
    }
}
