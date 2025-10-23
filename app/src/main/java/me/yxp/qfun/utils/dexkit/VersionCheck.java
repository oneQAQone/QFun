package me.yxp.qfun.utils.dexkit;

import java.util.HashMap;
import java.util.Map;

import me.yxp.qfun.BuildConfig;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.qq.HostInfo;

public class VersionCheck {
    public static boolean checkVersion() {
        Object obj = DataUtils.deserialize("dexkit", "lastVersionMap");
        if (obj != null) {
            Map<String, Long> lastVersionMap = (Map<String, Long>) obj;
            return (getModuleVersionCode() != lastVersionMap.get("module") ||
                    HostInfo.getVersionCode() != lastVersionMap.get("host"));
        }
        return true;
    }

    private static long getModuleVersionCode() {
        return Integer.toUnsignedLong(BuildConfig.VERSION_CODE);
    }

    public static Map<String, Long> getLatestVersionMap() {
        Map<String, Long> latestVersionMap = new HashMap<>();
        latestVersionMap.put("module", getModuleVersionCode());
        latestVersionMap.put("host", HostInfo.getVersionCode());
        return latestVersionMap;
    }
}