package com.tencent.qphone.base.util;

import com.tencent.mobileqq.qfix.ApplicationDelegate;

public abstract class BaseApplication extends ApplicationDelegate {
    public static BaseApplication getContext() {
        throw new UnsupportedOperationException();
    }

    public abstract Object getAppData(String str);

    public abstract int getAppId();

    public abstract String getChannelId();

    public abstract int getNTCoreVersion();

    public abstract String getQua();

    public abstract boolean isUserAllow();
}
