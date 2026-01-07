package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class VASGiftSpendCoinItem implements Serializable {
    public int amt;
    public int coinType;
    long serialVersionUID = 1;

    public VASGiftSpendCoinItem() {
    }

    public int getAmt() {
        return this.amt;
    }

    public int getCoinType() {
        return this.coinType;
    }

    public VASGiftSpendCoinItem(int i, int i2) {
        this.coinType = i;
        this.amt = i2;
    }
}
