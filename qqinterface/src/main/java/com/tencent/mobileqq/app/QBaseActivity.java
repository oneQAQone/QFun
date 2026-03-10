package com.tencent.mobileqq.app;

import android.app.Activity;

public class QBaseActivity extends Activity {
    public static QBaseActivity sTopActivity;
    protected boolean mShowOnFirst;
    public boolean isShowOnFirst() {
        return mShowOnFirst;
    }
}
