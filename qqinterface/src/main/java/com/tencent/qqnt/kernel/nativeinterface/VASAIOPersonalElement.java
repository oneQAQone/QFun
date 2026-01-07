package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;
import java.util.HashMap;

public final class VASAIOPersonalElement {
    public HashMap<String, String> extInfo;
    public Long troopNameColorId;
    public Integer vaDataChangeRand;
    public VASPersonalNamePlate vasPersonalNamePlate;
    public ArrayList<VASPersonalVipNumberInfo> vipNumbers;

    public VASAIOPersonalElement() {
        this.vipNumbers = new ArrayList<>();
    }

    public HashMap<String, String> getExtInfo() {
        return this.extInfo;
    }

    public Long getTroopNameColorId() {
        return this.troopNameColorId;
    }

    public Integer getVaDataChangeRand() {
        return this.vaDataChangeRand;
    }

    public VASPersonalNamePlate getVasPersonalNamePlate() {
        return this.vasPersonalNamePlate;
    }

    public ArrayList<VASPersonalVipNumberInfo> getVipNumbers() {
        return this.vipNumbers;
    }

    public VASAIOPersonalElement(Long l, ArrayList<VASPersonalVipNumberInfo> arrayList, Integer num, VASPersonalNamePlate vASPersonalNamePlate, HashMap<String, String> hashMap) {
        this.troopNameColorId = l;
        this.vipNumbers = arrayList;
        this.vaDataChangeRand = num;
        this.vasPersonalNamePlate = vASPersonalNamePlate;
        this.extInfo = hashMap;
    }
}
