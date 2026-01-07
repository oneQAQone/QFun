package com.tencent.qqnt.kernel.nativeinterface;

public final class StGPS {
    public long alt;
    public long eType;
    public long lat;
    public long lon;

    public StGPS() {
    }

    public StGPS(long j, long j2, long j3, long j4) {
        this.lat = j;
        this.lon = j2;
        this.eType = j3;
        this.alt = j4;
    }

    public long getAlt() {
        return this.alt;
    }

    public void setAlt(long j) {
        this.alt = j;
    }

    public long getEType() {
        return this.eType;
    }

    public void setEType(long j) {
        this.eType = j;
    }

    public long getLat() {
        return this.lat;
    }

    public void setLat(long j) {
        this.lat = j;
    }

    public long getLon() {
        return this.lon;
    }

    public void setLon(long j) {
        this.lon = j;
    }
}
