package com.tencent.qqnt.troop;

import com.tencent.mobileqq.data.troop.TroopInfo;
import com.tencent.mobileqq.qroute.QRouteApi;

import java.util.List;

public interface ITroopListRepoApi extends QRouteApi {
    List<TroopInfo> getSortedJoinedTroopInfoFromCache();
}
