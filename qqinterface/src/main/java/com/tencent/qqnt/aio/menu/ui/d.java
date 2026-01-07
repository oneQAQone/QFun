package com.tencent.qqnt.aio.menu.ui;

import androidx.annotation.Nullable;

import com.tencent.mobileqq.aio.msg.AIOMsgItem;

/**
 * 2025 / 08 / 28 （9.2.5）
 * 这个抽象类是被混淆的，但看了几个历史版本到目前最新版本都是相同的，先这样吧。
 */
public abstract class d {

    protected boolean a;
    protected int b;
    private AIOMsgItem c;

    public d(AIOMsgItem aIOMsgItem) {
        this.a = false;
        this.c = aIOMsgItem;
    }

    @Nullable
    public String a() {
        return null;
    }

    public abstract int b();

    public abstract int c();

    public AIOMsgItem d() {
        return this.c;
    }

    public abstract String e();

    public abstract String f();

    protected boolean g() {
        return false;
    }

    public abstract void h();
}
