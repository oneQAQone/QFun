package com.tencent.smtt.sdk;

public interface ValueCallback<T> extends android.webkit.ValueCallback<T> {
    @Override
    void onReceiveValue(T t);
}
