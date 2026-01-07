package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class RedPoint {
    public ArrayList<Entry> entry = new ArrayList<>();
    public long num;
    public int redPosition;
    public int redType;

    public ArrayList<Entry> getEntry() {
        return this.entry;
    }

    public long getNum() {
        return this.num;
    }

    public int getRedPosition() {
        return this.redPosition;
    }

    public int getRedType() {
        return this.redType;
    }

    public void setEntry(ArrayList<Entry> arrayList) {
        this.entry = arrayList;
    }

    public void setNum(long j) {
        this.num = j;
    }

    public void setRedPosition(int i) {
        this.redPosition = i;
    }

    public void setRedType(int i) {
        this.redType = i;
    }
}
