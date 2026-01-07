package com.tencent.qqnt.kernel.nativeinterface;

import java.util.HashMap;

public interface IGroupMemberUidCallback {
    void onResult(int i, String s, HashMap<Long, String> hashMap);
}
