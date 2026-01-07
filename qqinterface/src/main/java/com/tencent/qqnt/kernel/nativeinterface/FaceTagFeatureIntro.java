package com.tencent.qqnt.kernel.nativeinterface;

public final class FaceTagFeatureIntro {
    public boolean isDisplay;
    public String title = "";
    public String text = "";
    public String buttonText = "";
    public String imageUrl = "";
    public String darkImageUrl = "";

    public String getButtonText() {
        return this.buttonText;
    }

    public String getDarkImageUrl() {
        return this.darkImageUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public boolean getIsDisplay() {
        return this.isDisplay;
    }

    public String getText() {
        return this.text;
    }

    public String getTitle() {
        return this.title;
    }
}
