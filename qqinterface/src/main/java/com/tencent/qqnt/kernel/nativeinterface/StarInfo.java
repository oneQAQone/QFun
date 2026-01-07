package com.tencent.qqnt.kernel.nativeinterface;

public final class StarInfo {
    public boolean isAnnualVip;
    public boolean isHighStarVip;
    public int starLevel;
    public int starStatus;

    public boolean getIsAnnualVip() {
        return this.isAnnualVip;
    }

    public boolean getIsHighStarVip() {
        return this.isHighStarVip;
    }

    public int getStarLevel() {
        return this.starLevel;
    }

    public int getStarStatus() {
        return this.starStatus;
    }
}
