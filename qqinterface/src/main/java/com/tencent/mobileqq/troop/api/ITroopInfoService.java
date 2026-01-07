package com.tencent.mobileqq.troop.api;

import com.tencent.mobileqq.data.troop.TroopInfo;
import mqq.app.api.IRuntimeService;

public interface ITroopInfoService extends IRuntimeService {
    TroopInfo getTroopInfo(String troopUin);
}
