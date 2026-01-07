package com.tencent.qqnt.kernel.nativeinterface;

public final class MarketFaceSupportSize {
    public int height;
    public int width;

    public MarketFaceSupportSize() {
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public MarketFaceSupportSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }
}
