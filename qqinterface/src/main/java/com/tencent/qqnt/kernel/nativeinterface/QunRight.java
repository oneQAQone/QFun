package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class QunRight {
    public boolean normalUpload;
    public ArrayList<Integer> right = new ArrayList<>();

    public boolean getNormalUpload() {
        return this.normalUpload;
    }

    public ArrayList<Integer> getRight() {
        return this.right;
    }
}
