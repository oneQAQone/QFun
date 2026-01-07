package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class AdelieRecommendedMsgElement {
    public long botAppid;
    public InlineKeyboardStyle keyboardStyle;
    public ArrayList<InlineKeyboardRow> rows;

    public AdelieRecommendedMsgElement() {
        this.rows = new ArrayList<>();
        this.keyboardStyle = new InlineKeyboardStyle();
    }

    public long getBotAppid() {
        return this.botAppid;
    }

    public InlineKeyboardStyle getKeyboardStyle() {
        return this.keyboardStyle;
    }

    public ArrayList<InlineKeyboardRow> getRows() {
        return this.rows;
    }

    public AdelieRecommendedMsgElement(ArrayList<InlineKeyboardRow> arrayList, long j) {
        this.rows = new ArrayList<>();
        this.keyboardStyle = new InlineKeyboardStyle();
        this.rows = arrayList;
        this.botAppid = j;
    }
}
