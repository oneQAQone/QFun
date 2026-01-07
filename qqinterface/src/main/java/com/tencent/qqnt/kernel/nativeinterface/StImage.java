package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StImage {
    public StPicUrl defaultUrl;
    public boolean hasRaw;
    public boolean isGif;
    public String lloc;
    public String name;
    public ArrayList<StPicSpecUrlEntry> photoUrl;
    public String sloc;

    public StImage() {
        this.name = "";
        this.sloc = "";
        this.lloc = "";
        this.photoUrl = new ArrayList<>();
        this.defaultUrl = new StPicUrl();
    }

    public StPicUrl getDefaultUrl() {
        return this.defaultUrl;
    }

    public boolean getHasRaw() {
        return this.hasRaw;
    }

    public boolean getIsGif() {
        return this.isGif;
    }

    public String getLloc() {
        return this.lloc;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<StPicSpecUrlEntry> getPhotoUrl() {
        return this.photoUrl;
    }

    public String getSloc() {
        return this.sloc;
    }

    public void setDefaultUrl(StPicUrl stPicUrl) {
        this.defaultUrl = stPicUrl;
    }

    public void setHasRaw(boolean z) {
        this.hasRaw = z;
    }

    public void setIsGif(boolean z) {
        this.isGif = z;
    }

    public void setLloc(String str) {
        this.lloc = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setPhotoUrl(ArrayList<StPicSpecUrlEntry> arrayList) {
        this.photoUrl = arrayList;
    }

    public void setSloc(String str) {
        this.sloc = str;
    }

    public StImage(String str, String str2, String str3, ArrayList<StPicSpecUrlEntry> arrayList, StPicUrl stPicUrl, boolean z, boolean z2) {
        this.name = "";
        this.sloc = "";
        this.lloc = "";
        this.photoUrl = new ArrayList<>();
        this.name = str;
        this.sloc = str2;
        this.lloc = str3;
        this.photoUrl = arrayList;
        this.defaultUrl = stPicUrl;
        this.isGif = z;
        this.hasRaw = z2;
    }
}
