package com.tencent.qqnt.msg.api;


import com.tencent.mobileqq.qroute.QRouteApi;
import com.tencent.qqnt.kernel.nativeinterface.MsgElement;
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord;
import com.tencent.qqnt.kernel.nativeinterface.TextElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import kotlin.Pair;

public interface IMsgUtilApi extends QRouteApi {

    @NotNull
    MsgElement createAtTextElement(@NotNull String str, @NotNull String str2, int i);

    @NotNull
    MsgElement createFaceElement(int i, int i2, @NotNull String str);

    @NotNull
    MsgElement createFaceElement(int i, int i2, @NotNull String str, int i3, @NotNull String str2);

    @NotNull
    MsgElement createFileElement(@NotNull String str);

    @NotNull
    MsgElement createFileElement(@NotNull String str, int i);

    @NotNull
    MsgElement createGiphyElement(@NotNull String str, int i, int i2, boolean z);

    @NotNull
    MsgElement createPicElement(@NotNull String str, boolean z, int i);

    @NotNull
    MsgElement createPicElementForGuild(@NotNull String str, boolean z, int i);

    @NotNull
    MsgElement createPttElement(@NotNull String str, int i);

    @NotNull
    MsgElement createPttElement(@NotNull String str, int i, @NotNull ArrayList<Byte> arrayList);

    @NotNull
    MsgElement createReplyElement(long j);

    @NotNull
    MsgElement createReplyElement(long j, @NotNull String str, @NotNull String str2);

    @NotNull
    MsgElement createTextElement(@NotNull TextElement textElement);

    @NotNull
    MsgElement createTextElement(@NotNull String str);

    @NotNull
    MsgElement createVideoElement(@NotNull String str);

    @NotNull
    MsgElement createVideoElement(@NotNull String str, int i, boolean z, @Nullable String str2);

    @NotNull
    String getElementContent(@NotNull MsgElement msgElement);

    @NotNull
    String getElementSummary(@NotNull MsgRecord msgRecord);

    int getImageType(@NotNull String str);

    @NotNull
    Pair<Integer, Integer> getPicSizeByPath(@NotNull String str);

    boolean isArkElem(@NotNull MsgElement msgElement);

    boolean isPictureElem(@NotNull MsgElement msgElement);

    boolean isTextElem(@NotNull MsgElement msgElement);

    boolean isVideoElem(@NotNull MsgElement msgElement);

    @NotNull
    String msgTypeToString(int i);

    @NotNull
    String msgTypeToString(@NotNull MsgRecord msgRecord);
}
