package com.tencent.mobileqq.emoticonview.api;

import com.tencent.mobileqq.AIODepend.IPanelInteractionListener;
import com.tencent.mobileqq.emoticonview.IEmoticonMainPanel;
import com.tencent.mobileqq.qroute.QRouteApi;

public interface IEmosmService extends QRouteApi {
    IEmoticonMainPanel tryGetEmoticonMainPanel(IPanelInteractionListener iPanelInteractionListener);
}
