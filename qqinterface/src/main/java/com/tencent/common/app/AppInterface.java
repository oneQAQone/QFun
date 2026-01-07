package com.tencent.common.app;

import com.tencent.mobileqq.app.BusinessHandler;
import com.tencent.qphone.base.remote.ToServiceMsg;
import com.tencent.qphone.base.util.BaseApplication;

import mqq.app.AppRuntime;
import mqq.app.MobileQQ;

public abstract class AppInterface extends AppRuntime {
    public AppInterface(MobileQQ mobileQQ, String str) {
        throw new RuntimeException("Stub !");
    }

    public abstract BaseApplication getApp();

    public abstract int getAppid();

    public abstract String getCurrentAccountUin();

    public String getCurrentNickname() {
        return "";
    }

    public BusinessHandler getBusinessHandler(String className) {
        return null;
    }

    public void sendToService(ToServiceMsg toServiceMsg) {
    }
}
