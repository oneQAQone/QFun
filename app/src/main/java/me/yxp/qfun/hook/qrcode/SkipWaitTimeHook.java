package me.yxp.qfun.hook.qrcode;

import android.widget.Button;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedHelpers;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "跳过扫码确认等待时间", desc = "可忽略倒计时，直接点击确认即可")
public final class SkipWaitTimeHook extends BaseSwitchHookItem {

    private static Method sDoOnCreate;
    private static Class<?> sQUIButton;

    @Override
    protected boolean initMethod() throws Throwable {

        sQUIButton = ClassUtils.load("com.tencent.biz.qui.quibutton.QUIButton");

        sDoOnCreate = MethodUtils.create(ClassUtils._QrLoginAuthActivity())
                .withMethodName("doOnCreate")
                .findOne();
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sDoOnCreate, null, param -> {
            Button confirmButton = (Button) FieldUtils.create(param.thisObject)
                    .ofType(sQUIButton).getValue();
            SyncUtils.postDelayed(() -> {
                confirmButton.setEnabled(true);
                XposedHelpers.callMethod(confirmButton, "setType", 0);
            }, 100);
        });
    }
}
