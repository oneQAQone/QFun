package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public interface IKickMemberOperateCallback {
    void onResult(int i, String str, ArrayList<KickMemberResult> arrayList);
}