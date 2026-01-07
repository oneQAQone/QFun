package com.tencent.qqnt.kernel.nativeinterface;

public final class StLBS {
    public StGPS gps = new StGPS();
    public String location = "";
    public String lbsId = "";
    public String address = "";

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public StGPS getGps() {
        return this.gps;
    }

    public void setGps(StGPS stGPS) {
        this.gps = stGPS;
    }

    public String getLbsId() {
        return this.lbsId;
    }

    public void setLbsId(String str) {
        this.lbsId = str;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String str) {
        this.location = str;
    }
}
