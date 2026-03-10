package com.tencent.mobileqq.emosm.api;

import com.tencent.mobileqq.data.CustomEmotionData;

import java.util.List;

import mqq.app.AppRuntime;
import mqq.app.api.IRuntimeService;

public interface IFavroamingDBManagerService extends IRuntimeService {
    List<CustomEmotionData> getEmoticonDataList();
    void trimCache();
    void insertCustomEmotion(CustomEmotionData customEmotionData);
    void deleteCustomEmotion(CustomEmotionData customEmotionData);
}
