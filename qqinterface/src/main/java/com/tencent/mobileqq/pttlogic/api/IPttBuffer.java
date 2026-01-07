package com.tencent.mobileqq.pttlogic.api;

import com.tencent.mobileqq.qroute.QRouteApi;

import java.util.Map;

public interface IPttBuffer extends QRouteApi {
    boolean appendBuffer(String str, byte[] bArr, int i);

    void cancelBufferTask(String str);

    boolean createBufferTask(String str);

    boolean flush(String str);

    Map getTaskMap();

    void setMaxBufferSize(int i);
}