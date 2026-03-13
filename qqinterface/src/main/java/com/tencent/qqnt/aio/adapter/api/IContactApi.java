package com.tencent.qqnt.aio.adapter.api;

import android.content.Context;

import com.tencent.mobileqq.aio.msg.AIOMsgItem;
import com.tencent.mobileqq.qroute.QRouteApi;

public interface IContactApi extends QRouteApi {
    void openProfileCard(Context context, AIOMsgItem aioMsgItem);
}
