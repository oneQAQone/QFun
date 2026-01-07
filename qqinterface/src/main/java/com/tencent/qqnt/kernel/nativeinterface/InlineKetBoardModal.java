package com.tencent.qqnt.kernel.nativeinterface;

public final class InlineKetBoardModal {
    public String content = "";
    public String confirmText = "";
    public String cancelText = "";

    public String getCancelText() {
        return this.cancelText;
    }

    public String getConfirmText() {
        return this.confirmText;
    }

    public String getContent() {
        return this.content;
    }
}
