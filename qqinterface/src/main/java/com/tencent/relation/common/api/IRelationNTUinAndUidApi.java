package com.tencent.relation.common.api;

import com.tencent.mobileqq.qroute.QRouteApi;

public interface IRelationNTUinAndUidApi extends QRouteApi {
    String getUidFromUin(String uin);
    String getUinFromUid(String uid);
}
