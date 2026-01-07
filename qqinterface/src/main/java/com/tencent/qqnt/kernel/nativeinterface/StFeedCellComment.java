package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StFeedCellComment {
    public ArrayList<StComment> comments = new ArrayList<>();
    public int num;

    public ArrayList<StComment> getComments() {
        return this.comments;
    }

    public int getNum() {
        return this.num;
    }
}
