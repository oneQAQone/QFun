package me.yxp.qfun.hook.base;

import android.view.View;

@HookItemAnnotation(TAG = "基础可点击项")
public abstract class BaseWithDataHookItem extends BaseSwitchHookItem implements View.OnClickListener {

    public abstract void initData();

    public abstract void savaData();

    @Override
    public abstract void onClick(View v);

}

