package com.tencent.qqnt.kernel.nativeinterface;

public final class AddBuddyDeviceInfo {
    public String deviceName;
    public String guid;
    public String operatingSystem;

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getGuid() {
        return this.guid;
    }

    public String getOperatingSystem() {
        return this.operatingSystem;
    }
}
