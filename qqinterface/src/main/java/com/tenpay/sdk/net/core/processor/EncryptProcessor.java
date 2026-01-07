package com.tenpay.sdk.net.core.processor;

import com.tenpay.sdk.net.core.comm.SessionKey;
import com.tenpay.sdk.net.core.encrypt.DecryptResult;
import com.tenpay.sdk.net.core.encrypt.EncryptResult;
import com.tenpay.sdk.net.core.statistic.StatisticInfo;

import java.util.HashMap;
import java.util.Map;

public class EncryptProcessor {
    public final ProcessResult processEncrypt(String str, SessionKey sessionKey, Map<String, String> map, boolean z1, boolean z2, StatisticInfo statisticInfo) {
        return null;
    }

    public final DecryptResult processDecrypt(boolean z1, boolean z2, SessionKey sessionKey, EncryptResult encryptResult, StatisticInfo statisticInfo, String s) {
        return null;
    }

    public static class ProcessResult {
        private final Map<String, String> bodyData = new HashMap<String, String>();

        public final Map<String, String> getBodyData() {
            return this.bodyData;
        }

        public final EncryptResult getEncryptResult() {
            return null;
        }
    }
}
