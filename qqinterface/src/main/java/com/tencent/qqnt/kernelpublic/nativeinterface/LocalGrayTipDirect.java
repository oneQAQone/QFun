package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class LocalGrayTipDirect implements Serializable {
    public String robotName;
    public long robotTid;
    public long robotUin;
    long serialVersionUID;

    public LocalGrayTipDirect() {
        this.serialVersionUID = 1L;
        this.robotName = "";
    }

    public String getRobotName() {
        return this.robotName;
    }

    public long getRobotTid() {
        return this.robotTid;
    }

    public long getRobotUin() {
        return this.robotUin;
    }

    public LocalGrayTipDirect(long j, long j2, String str) {
        this.serialVersionUID = 1L;
        this.robotTid = j;
        this.robotUin = j2;
        this.robotName = str;
    }
}
