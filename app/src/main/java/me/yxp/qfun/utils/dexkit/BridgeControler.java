package me.yxp.qfun.utils.dexkit;

import org.luckypray.dexkit.DexKitBridge;

public class BridgeControler {
    private static DexKitBridge sBridge;

    public static void initBridge(String path) {
        System.loadLibrary("dexkit");
        sBridge = DexKitBridge.create(path);
    }

    public static DexKitBridge getBridge() {
        return sBridge;
    }

    public static void closeBridge() {
        if (sBridge != null) {
            sBridge.close();
        }
    }
}