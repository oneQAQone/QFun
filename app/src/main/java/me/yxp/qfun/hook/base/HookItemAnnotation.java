package me.yxp.qfun.hook.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HookItemAnnotation {

    String TAG();

    String desc() default "";

    String TargetProcess() default "";

}
