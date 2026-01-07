# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 保持模块自身的所有类和成员
-keep class me.yxp.qfun.** { *; }

-keep class bsh.** { *; }
# 保持 Kotlin 函数类及其所有成员
-keep class kotlin.jvm.functions.** { *; }

# 保持所有包含原生方法的类
-keepclasseswithmembernames class * {
    native <methods>;
}


# Xposed
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keepattributes RuntimeVisibleAnnotations
-keep,allowobfuscation,allowoptimization public class * extends io.github.libxposed.api.XposedModule {
    public <init>(...);
    public void onPackageLoaded(...);
    public void onSystemServerLoaded(...);
}
-keep,allowoptimization,allowobfuscation @io.github.libxposed.api.annotations.* class * {
    @io.github.libxposed.api.annotations.BeforeInvocation <methods>;
    @io.github.libxposed.api.annotations.AfterInvocation <methods>;
}

# 保持必要的属性和注解
-keepattributes LineNumberTable,SourceFile,RuntimeVisibleAnnotations,AnnotationDefault


# 保持 Xposed API 相关的类
-keep class android.view.animation.PathInterpolator { *; }

# 忽略 Xposed 和 ByteBuddy 相关的警告
-dontwarn de.robv.android.xposed.**
-dontwarn io.github.libxposed.api.**

# Kotlin Intrinsics 方法没有副作用，可以安全移除
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# Java Objects.requireNonNull 方法没有副作用，可以安全移除
-assumenosideeffects class java.util.Objects {
    public static ** requireNonNull(...);
}

#-keep class com.android.dx.** {*;}
-keep class net.bytebuddy.** {
    *;
}

#-keep class com.google.protobuf.**
# protobuf
-keepclassmembers public class * extends com.google.protobuf.MessageLite {*;}
-keepclassmembers public class * extends com.google.protobuf.MessageOrBuilder {*;}

-keepattributes *Annotation*

-dontwarn javax.**
-dontwarn java.awt.**
-dontwarn org.apache.bsf.*

# ByteBuddy混淆
-dontwarn com.sun.jna.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn java.lang.instrument.**

-keepattributes LineNumberTable,SourceFile

-dontoptimize
-dontobfuscate