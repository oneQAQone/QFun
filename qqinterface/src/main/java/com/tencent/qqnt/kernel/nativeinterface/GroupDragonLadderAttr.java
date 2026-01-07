package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GroupDragonLadderAttr {
    public long dragonLadderId;
    public ArrayList<Integer> selfItemOffsets = new ArrayList<>();

    public long getDragonLadderId() {
        return this.dragonLadderId;
    }

    public ArrayList<Integer> getSelfItemOffsets() {
        return this.selfItemOffsets;
    }
}
