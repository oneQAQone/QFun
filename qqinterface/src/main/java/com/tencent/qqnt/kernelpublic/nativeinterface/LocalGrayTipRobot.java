package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class LocalGrayTipRobot implements Serializable {
    public boolean isBlackRobot;
    public long robotTid;
    long serialVersionUID = 1;

    public LocalGrayTipRobot() {
    }

    public boolean getIsBlackRobot() {
        return this.isBlackRobot;
    }

    public long getRobotTid() {
        return this.robotTid;
    }

    public LocalGrayTipRobot(long j, boolean z) {
        this.robotTid = j;
        this.isBlackRobot = z;
    }
}
