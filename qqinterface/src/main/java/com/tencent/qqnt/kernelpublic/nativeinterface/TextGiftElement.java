package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;
import java.util.ArrayList;

public final class TextGiftElement implements Serializable {
    public String bgImageUrl;
    public int charmValue;
    public long giftId;
    public String giftName;
    public int level;
    public boolean needPlayAnimation;
    public String orderId;
    public String paddingTop;
    public long price;
    public String receiverNick;
    public long receiverUin;
    public int sendType;
    public String senderNick;
    public long senderUin;
    long serialVersionUID;
    public ArrayList<VASGiftSpendCoinItem> spendCoins;
    public long tianquanId;

    public TextGiftElement() {
        this.serialVersionUID = 1L;
        this.giftName = "";
        this.receiverNick = "";
        this.senderNick = "";
        this.orderId = "";
        this.bgImageUrl = "";
        this.paddingTop = "";
        this.spendCoins = new ArrayList<>();
    }

    public String getBgImageUrl() {
        return this.bgImageUrl;
    }

    public int getCharmValue() {
        return this.charmValue;
    }

    public long getGiftId() {
        return this.giftId;
    }

    public String getGiftName() {
        return this.giftName;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean getNeedPlayAnimation() {
        return this.needPlayAnimation;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public String getPaddingTop() {
        return this.paddingTop;
    }

    public long getPrice() {
        return this.price;
    }

    public String getReceiverNick() {
        return this.receiverNick;
    }

    public long getReceiverUin() {
        return this.receiverUin;
    }

    public int getSendType() {
        return this.sendType;
    }

    public String getSenderNick() {
        return this.senderNick;
    }

    public long getSenderUin() {
        return this.senderUin;
    }

    public ArrayList<VASGiftSpendCoinItem> getSpendCoins() {
        return this.spendCoins;
    }

    public long getTianquanId() {
        return this.tianquanId;
    }

    public TextGiftElement(long j, String str, long j2, long j3, String str2, String str3, long j4, String str4, String str5, long j5, int i, String str6, ArrayList<VASGiftSpendCoinItem> arrayList, boolean z, int i2, int i3) {
        this.serialVersionUID = 1L;
        this.giftName = "";
        this.receiverNick = "";
        this.senderNick = "";
        this.orderId = "";
        this.bgImageUrl = "";
        this.paddingTop = "";
        this.giftId = j;
        this.giftName = str;
        this.receiverUin = j2;
        this.senderUin = j3;
        this.receiverNick = str2;
        this.senderNick = str3;
        this.price = j4;
        this.orderId = str4;
        this.bgImageUrl = str5;
        this.tianquanId = j5;
        this.level = i;
        this.paddingTop = str6;
        this.spendCoins = arrayList;
        this.needPlayAnimation = z;
        this.sendType = i2;
        this.charmValue = i3;
    }
}
