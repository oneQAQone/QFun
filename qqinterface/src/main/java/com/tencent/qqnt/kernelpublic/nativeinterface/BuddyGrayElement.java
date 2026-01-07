package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class BuddyGrayElement implements Serializable {
    public NewBuddyGrayElement elem;
    long serialVersionUID = 1;
    public int type;

    public BuddyGrayElement() {
    }

    public NewBuddyGrayElement getElem() {
        return this.elem;
    }

    public int getType() {
        return this.type;
    }

    public BuddyGrayElement(int i, NewBuddyGrayElement newBuddyGrayElement) {
        this.type = i;
        this.elem = newBuddyGrayElement;
    }
}
