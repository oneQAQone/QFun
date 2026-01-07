package com.tencent.qqnt.kernel.nativeinterface;

public final class StFeedCellUserInfo {
    public StUser user;

    public StFeedCellUserInfo() {
        this.user = new StUser();
    }

    public StUser getUser() {
        return this.user;
    }

    public void setUser(StUser stUser) {
        this.user = stUser;
    }

    public StFeedCellUserInfo(StUser stUser) {
        new StUser();
        this.user = stUser;
    }
}
