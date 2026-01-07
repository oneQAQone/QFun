package com.tencent.mobileqq.app;

import com.tencent.common.app.AppInterface;
import com.tencent.common.app.BaseApplicationImpl;
import com.tencent.qphone.base.util.BaseApplication;

public class QQAppInterface extends AppInterface {

    public QQAppInterface(BaseApplicationImpl baseApplication, String str) {
        super(baseApplication, str);
    }

    @Override
    public BaseApplication getApp() {
        throw new RuntimeException("Stub !");
    }

    @Override
    public int getAppid() {
        throw new RuntimeException("Stub !");
    }

    @Override
    public String getCurrentAccountUin() {
        throw new RuntimeException("Stub !");
    }
}
