package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class PlainTxtInfo {
    public ArrayList<StRichMsg> txtInfo = new ArrayList<>();

    public ArrayList<StRichMsg> getTxtInfo() {
        return this.txtInfo;
    }
}
