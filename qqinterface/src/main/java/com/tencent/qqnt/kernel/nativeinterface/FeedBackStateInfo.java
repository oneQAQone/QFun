package com.tencent.qqnt.kernel.nativeinterface;

public final class FeedBackStateInfo {
    public int likeOrDislike;

    public FeedBackStateInfo() {
    }

    public int getLikeOrDislike() {
        return this.likeOrDislike;
    }

    public FeedBackStateInfo(int i) {
        this.likeOrDislike = i;
    }
}
