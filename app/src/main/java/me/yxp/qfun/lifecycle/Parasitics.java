/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */

package me.yxp.qfun.lifecycle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.loader.ResourcesLoader;
import android.content.res.loader.ResourcesProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import me.yxp.qfun.BuildConfig;
import me.yxp.qfun.R;
import me.yxp.qfun.common.ModuleLoader;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;

@SuppressWarnings("JavaReflectionMemberAccess")
public class Parasitics {

    private static final String STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";

    private static final String ACTIVITY_PROXY_INTENT = "ACTIVITY_PROXY_INTENT";

    private static final ClassLoader moduleloader = ClassUtils.INSTANCE.getModuleClassLoader();

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static boolean isTargetActivity(String className) {

        if (className == null) return false;

        if (!className.startsWith(BuildConfig.APPLICATION_ID)) {
            return false;
        }

        try {
            Class<?> targetClass = moduleloader.loadClass(className);
            Class<?> baseClass = moduleloader.loadClass("me.yxp.qfun.activity.BaseComposeActivity");
            return baseClass.isAssignableFrom(targetClass);
        } catch (ClassNotFoundException e) {
            return false;
        }

    }


    public static void injectModuleResources(Resources res) {
        if (res == null) {
            return;
        }
        try {
            res.getString(R.string.app_name);
            return;
        } catch (Resources.NotFoundException ignored) {
        }
        String sModulePath = ModuleLoader.getMODULE_PATH();
        if (Build.VERSION.SDK_INT >= 30) {
            injectResourcesAboveApi30(res, sModulePath);
        } else {
            injectResourcesBelowApi30(res, sModulePath);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void injectResourcesAboveApi30(Resources res, String path) {
        if (ResourcesLoaderHolderApi30.sResourcesLoader == null) {
            try {
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(path),
                        ParcelFileDescriptor.MODE_READ_ONLY);
                ResourcesProvider provider = ResourcesProvider.loadFromApk(pfd);
                ResourcesLoader loader = new ResourcesLoader();
                loader.addProvider(provider);
                ResourcesLoaderHolderApi30.sResourcesLoader = loader;
            } catch (IOException e) {
            }
        }
        Runnable addLoaderTask = () -> {
            try {
                res.addLoaders(ResourcesLoaderHolderApi30.sResourcesLoader);
            } catch (IllegalArgumentException e) {
                String expected1 = "Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager";
                if (expected1.equals(e.getMessage())) {
                    injectResourcesBelowApi30(res, path);
                } else {
                    throw e;
                }
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            addLoaderTask.run();
        } else {
            mainHandler.post(addLoaderTask);
        }
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    private static void injectResourcesBelowApi30(Resources res, String path) {
        try {
            AssetManager assets = res.getAssets();
            @SuppressLint("DiscouragedPrivateApi") Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assets, path);
        } catch (Exception e) {
        }
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static void initForStubActivity(Context ctx) {

        try {
            Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz_ActivityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object sCurrentActivityThread = currentActivityThread.invoke(null);
            Field mInstrumentation = clazz_ActivityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(sCurrentActivityThread);
            mInstrumentation.set(sCurrentActivityThread, new ProxyInstrumentation(instrumentation));

            Field field_mH = clazz_ActivityThread.getDeclaredField("mH");
            field_mH.setAccessible(true);
            Handler oriHandler = (Handler) field_mH.get(sCurrentActivityThread);
            Field field_mCallback = Handler.class.getDeclaredField("mCallback");
            field_mCallback.setAccessible(true);
            Handler.Callback current = (Handler.Callback) field_mCallback.get(oriHandler);
            if (current == null || !current.getClass().getName().equals(ProxyHandlerCallback.class.getName())) {
                field_mCallback.set(oriHandler, new ProxyHandlerCallback(current));
            }

            Class<?> activityManagerClass;
            Field gDefaultField;
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerClass.getDeclaredField("gDefault");
            } catch (Exception err1) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager");
                    gDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
                } catch (Exception err2) {

                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(gDefault);
            Object amProxy = Proxy.newProxyInstance(moduleloader,
                    new Class[]{Class.forName("android.app.IActivityManager")}, new IActivityManagerHandler(mInstance));
            mInstanceField.set(gDefault, amProxy);

            try {
                Class<?> activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
                Field fIActivityTaskManagerSingleton = activityTaskManagerClass
                        .getDeclaredField("IActivityTaskManagerSingleton");
                fIActivityTaskManagerSingleton.setAccessible(true);
                Object singleton = fIActivityTaskManagerSingleton.get(null);
                singletonClass.getMethod("get").invoke(singleton);
                Object mDefaultTaskMgr = mInstanceField.get(singleton);
                Object proxy2 = Proxy.newProxyInstance(moduleloader,
                        new Class[]{Class.forName("android.app.IActivityTaskManager")},
                        new IActivityManagerHandler(mDefaultTaskMgr));
                mInstanceField.set(singleton, proxy2);
            } catch (Exception err3) {
            }

            Field sPackageManagerField = clazz_ActivityThread.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object packageManagerImpl = sPackageManagerField.get(sCurrentActivityThread);
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            PackageManager pm = ctx.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            Object pmProxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class[]{iPackageManagerInterface}, new PackageManagerInvocationHandler(packageManagerImpl));
            sPackageManagerField.set(currentActivityThread, pmProxy);
            mPmField.set(pm, pmProxy);

        } catch (Exception e) {
        }
    }

    private static class ResourcesLoaderHolderApi30 {

        public static ResourcesLoader sResourcesLoader = null;

        private ResourcesLoaderHolderApi30() {
        }

    }

    private record IActivityManagerHandler(Object mOrigin) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("startActivity".equals(method.getName())) {
                int index = -1;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    Intent raw = (Intent) args[index];
                    ComponentName component = raw.getComponent();

                    if (component != null && HostInfo.INSTANCE.getPackageName().equals(component.getPackageName())
                            && isTargetActivity(component.getClassName())) {

                        Intent wrapper = new Intent();

                        wrapper.setClassName(component.getPackageName(), STUB_DEFAULT_ACTIVITY);
                        wrapper.putExtra(ACTIVITY_PROXY_INTENT, raw);
                        args[index] = wrapper;
                    }
                }
            }
            try {
                return method.invoke(mOrigin, args);
            } catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
    }

    private record ProxyHandlerCallback(
            Handler.Callback mNextCallbackHook) implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                onHandleLaunchActivity(msg);
            } else if (msg.what == 159) {
                onHandleExecuteTransaction(msg);
            }
            if (mNextCallbackHook != null) {
                return mNextCallbackHook.handleMessage(msg);
            }
            return false;
        }

        @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
        private void onHandleLaunchActivity(Message msg) {
            try {
                Object activityClientRecord = msg.obj;
                Field field_intent = activityClientRecord.getClass().getDeclaredField("intent");
                field_intent.setAccessible(true);
                Intent intent = (Intent) field_intent.get(activityClientRecord);
                assert intent != null;
                Bundle bundle = null;
                Intent cloneIntent = new Intent(intent);
                try {
                    Field fExtras = Intent.class.getDeclaredField("mExtras");
                    fExtras.setAccessible(true);
                    bundle = (Bundle) fExtras.get(cloneIntent);
                } catch (Exception e) {
                }
                if (bundle != null) {
                    bundle.setClassLoader(ClassUtils.INSTANCE.getHostClassLoader());
                    if (cloneIntent.hasExtra(ACTIVITY_PROXY_INTENT)) {
                        Intent realIntent = cloneIntent.getParcelableExtra(ACTIVITY_PROXY_INTENT);
                        field_intent.set(activityClientRecord, realIntent);
                    }
                }
            } catch (Exception e) {
            }
        }

        @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
        private void onHandleExecuteTransaction(Message msg) {
            Object clientTransaction = msg.obj;
            try {
                if (clientTransaction != null) {
                    Method getCallbacks = Class.forName("android.app.servertransaction.ClientTransaction")
                            .getDeclaredMethod("getCallbacks");
                    getCallbacks.setAccessible(true);
                    List<?> clientTransactionItems = (List<?>) getCallbacks.invoke(clientTransaction);
                    if (clientTransactionItems != null && !clientTransactionItems.isEmpty()) {
                        for (Object item : clientTransactionItems) {
                            Class<?> c = item.getClass();
                            if (c.getName().contains("LaunchActivityItem")) {
                                processLaunchActivityItem(item, clientTransaction);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
        private void processLaunchActivityItem(Object item, Object clientTransaction)
                throws ReflectiveOperationException {
            Class<?> c = item.getClass();
            Field fmIntent = c.getDeclaredField("mIntent");
            fmIntent.setAccessible(true);
            Intent wrapper = (Intent) fmIntent.get(item);
            assert wrapper != null;
            Intent cloneIntent = (Intent) wrapper.clone();
            Bundle bundle = null;
            try {
                Field fExtras = Intent.class.getDeclaredField("mExtras");
                fExtras.setAccessible(true);
                bundle = (Bundle) fExtras.get(cloneIntent);
            } catch (Exception e) {
            }
            if (bundle != null) {
                bundle.setClassLoader(moduleloader);
                if (cloneIntent.hasExtra(ACTIVITY_PROXY_INTENT)) {
                    Intent realIntent = cloneIntent.getParcelableExtra(ACTIVITY_PROXY_INTENT);
                    fmIntent.set(item, realIntent);
                    if (Build.VERSION.SDK_INT >= 31) {
                        IBinder token = (IBinder) clientTransaction.getClass().getMethod("getActivityToken")
                                .invoke(clientTransaction);
                        Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
                        Method currentActivityThread = clazz_ActivityThread.getDeclaredMethod("currentActivityThread");
                        currentActivityThread.setAccessible(true);
                        Object activityThread = currentActivityThread.invoke(null);
                        assert activityThread != null;
                        try {
                            Object acr = activityThread.getClass().getMethod("getLaunchingActivity", IBinder.class)
                                    .invoke(activityThread, token);
                            if (acr != null) {
                                Field fAcrIntent = acr.getClass().getDeclaredField("intent");
                                fAcrIntent.setAccessible(true);
                                fAcrIntent.set(acr, realIntent);
                            }
                        } catch (NoSuchMethodException e) {
                            if (Build.VERSION.SDK_INT >= 33) {
                            } else {
                                throw e;
                            }
                        }
                    }
                }
            }
        }
    }

    private static class ProxyInstrumentation extends Instrumentation {

        private final Instrumentation mBase;

        public ProxyInstrumentation(Instrumentation base) {
            this.mBase = base;
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent)
                throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            try {
                return mBase.newActivity(cl, className, intent);
            } catch (Exception e) {
                ClassLoader selfClassLoader = Parasitics.class.getClassLoader();
                assert selfClassLoader != null;
                return (Activity) selfClassLoader.loadClass(className).newInstance();
            }
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle) {
            if (icicle != null) {
                String className = activity.getClass().getName();
                if (isTargetActivity(className)) {
                    icicle.setClassLoader(moduleloader);
                }
            }
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle);
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
            if (icicle != null) {
                String className = activity.getClass().getName();
                if (isTargetActivity(className)) {
                    icicle.setClassLoader(moduleloader);
                }
            }
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle, persistentState);
        }

    }

    private record PackageManagerInvocationHandler(Object target) implements InvocationHandler {

        private PackageManagerInvocationHandler {
            if (target == null) {
                throw new NullPointerException("IPackageManager == null");
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if ("getActivityInfo".equals(method.getName())) {
                    ActivityInfo ai = (ActivityInfo) method.invoke(target, args);
                    if (ai != null) {
                        return ai;
                    }
                    ComponentName component = (ComponentName) args[0];
                    long flags = ((Number) args[1]).longValue();
                    if (HostInfo.INSTANCE.getPackageName().equals(component.getPackageName())
                            && isTargetActivity(component.getClassName())) {
                        return CounterfeitActivityInfoFactory.makeProxyActivityInfo(component.getClassName(), flags);
                    } else {
                        return null;
                    }
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
    }
}
