package me.yxp.qfun.hook.purify;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "屏蔽链接预览", desc = "屏蔽连接文本的预览信息")
public final class AntiLinkPreviewHooK extends BaseSwitchHookItem {

    private static Method addPreview;

    @Override
    protected boolean initMethod() throws Throwable {

        Class<?> LinkInfo = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.LinkInfo");

        addPreview = MethodUtils.create(ClassUtils._TextMsgContent())
                .withReturnType(void.class)
                .withParamTypes(null, LinkInfo)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {

        HookUtils.replaceIfEnable(this, addPreview, param -> null);


    }
}
