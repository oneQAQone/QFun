package com.tencent.qqnt.troopmemberlist.cache;

import mqq.app.api.IRuntimeService;

public interface ITroopMemberCacheService extends IRuntimeService {
    String getUidFromUin(String uin);
    String getUinFromUid(String uid);
}
