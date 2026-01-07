package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;
import java.util.HashMap;

public interface IKernelProfileService {
    HashMap<Long, String> getUidByUin(String s, ArrayList<Long> arrayList);
    HashMap<String, Long> getUinByUid(String s, ArrayList<String> arrayList);
}
