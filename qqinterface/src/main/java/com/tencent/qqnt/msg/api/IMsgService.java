package com.tencent.qqnt.msg.api;

import com.tencent.mobileqq.qroute.QRouteApi;
import com.tencent.qqnt.kernel.nativeinterface.IOperateCallback;
import com.tencent.qqnt.kernel.nativeinterface.MsgAttributeInfo;
import com.tencent.qqnt.kernel.nativeinterface.MsgElement;
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public interface IMsgService extends QRouteApi {
    void sendMsg(@NotNull Contact contact, long msgId, @NotNull ArrayList<MsgElement> arrayList, @Nullable IOperateCallback iOperateCallback);

    void sendMsg(@NotNull Contact contact, @NotNull ArrayList<MsgElement> arrayList, @Nullable IOperateCallback iOperateCallback);

    void sendMsg(@NotNull Contact contact, @NotNull ArrayList<MsgElement> arrayList, @NotNull HashMap<Integer, MsgAttributeInfo> hashMap, @Nullable IOperateCallback iOperateCallback);

    void sendMsgWithMsgId(@NotNull Contact contact, long msgId, @NotNull ArrayList<MsgElement> arrayList, @Nullable IOperateCallback iOperateCallback);

    void sendSummonMsg(@NotNull Contact contact, @NotNull ArrayList<MsgElement> arrayList, @Nullable IOperateCallback iOperateCallback);
}
