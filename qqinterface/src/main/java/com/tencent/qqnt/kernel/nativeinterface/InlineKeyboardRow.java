package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class InlineKeyboardRow {
    public ArrayList<InlineKeyboardButton> buttons;

    public InlineKeyboardRow() {
        this.buttons = new ArrayList<>();
    }

    public ArrayList<InlineKeyboardButton> getButtons() {
        return this.buttons;
    }

    public InlineKeyboardRow(ArrayList<InlineKeyboardButton> arrayList) {
        this.buttons = arrayList;
    }
}
