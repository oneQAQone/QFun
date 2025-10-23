/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2024 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is an opensource software: you can redistribute it
 * and/or modify it under the terms of the General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the General Public License for more details.
 *
 * You should have received a copy of the General Public License
 * along with this software.
 * If not, see
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */

package me.yxp.qfun.loader.lsp100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import me.yxp.qfun.BuildConfig;
import me.yxp.qfun.common.CheckUtils;
import me.yxp.qfun.loader.hookapi.IClassLoaderHelper;
import me.yxp.qfun.loader.hookapi.IHookBridge;
import me.yxp.qfun.loader.hookapi.ILoaderService;

public class Lsp100HookImpl implements IHookBridge, ILoaderService {

    public static final Lsp100HookImpl INSTANCE = new Lsp100HookImpl();
    public static XposedModule self = null;
    private IClassLoaderHelper mClassLoaderHelper;

    private Lsp100HookImpl() {
    }

    public static void init(@NonNull XposedModule base) {
        self = base;
        Lsp100HookWrapper.self = base;
    }

    @Override
    public int getApiLevel() {
        return XposedInterface.API;
    }

    @NonNull
    @Override
    public String getFrameworkName() {
        return self.getFrameworkName();
    }

    @NonNull
    @Override
    public String getFrameworkVersion() {
        return self.getFrameworkVersion();
    }

    @Override
    public long getFrameworkVersionCode() {
        return self.getFrameworkVersionCode();
    }

    @NonNull
    @Override
    public MemberUnhookHandle hookMethod(@NonNull Member member, @NonNull IMemberHookCallback callback, int priority) {
        return Lsp100HookWrapper.hookAndRegisterMethodCallback(member, callback, priority);
    }

    @Override
    public boolean isDeoptimizationSupported() {
        return true;
    }

    @Override
    public boolean deoptimize(@NonNull Member member) {
        CheckUtils.checkNonNull(member, "member");
        if (member instanceof Method) {
            return self.deoptimize((Method) member);
        } else if (member instanceof Constructor) {
            return self.deoptimize((Constructor<?>) member);
        } else {
            throw new IllegalArgumentException("only method and constructor can be deoptimized");
        }
    }

    @Nullable
    @Override
    public Object invokeOriginalMethod(@NonNull Method method, @Nullable Object thisObject, @NonNull Object[] args)
            throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        CheckUtils.checkNonNull(method, "method");
        CheckUtils.checkNonNull(args, "args");
        return self.invokeOrigin(method, thisObject, args);
    }

    @NonNull
    @Override
    public <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, @NonNull Object... args)
            throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        CheckUtils.checkNonNull(constructor, "constructor");
        CheckUtils.checkNonNull(args, "args");
        return self.newInstanceOrigin(constructor, args);
    }

    @Override
    public <T> void invokeOriginalConstructor(@NonNull Constructor<T> ctor, @NonNull T thisObject, @NonNull Object[] args)
            throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        CheckUtils.checkNonNull(ctor, "ctor");
        CheckUtils.checkNonNull(thisObject, "thisObject");
        CheckUtils.checkNonNull(args, "args");
        self.invokeOrigin(ctor, thisObject, args);
    }

    @Nullable
    @Override
    public Object queryExtension(@NonNull String key, @Nullable Object... args) {
        return Lsp100ExtCmd.handleQueryExtension(key, args);
    }

    @NonNull
    @Override
    public String getEntryPointName() {
        return this.getClass().getName();
    }

    @NonNull
    @Override
    public String getLoaderVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public int getLoaderVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @NonNull
    @Override
    public String getMainModulePath() {
        return self.getApplicationInfo().sourceDir;
    }

    @Override
    public void log(@NonNull String msg) {
        self.log(msg);
    }

    @Override
    public void log(@NonNull Throwable tr) {
        self.log(tr.toString(), tr);
    }

    @Nullable
    @Override
    public IClassLoaderHelper getClassLoaderHelper() {
        return mClassLoaderHelper;
    }

    @Override
    public void setClassLoaderHelper(@Nullable IClassLoaderHelper helper) {
        mClassLoaderHelper = helper;
    }

    @Override
    public long getHookCounter() {
        return Lsp100HookWrapper.getHookCounter();
    }

    @Override
    public Set<Member> getHookedMethods() {
        // return a read-only set
        return Collections.unmodifiableSet(Lsp100HookWrapper.getHookedMethodsRaw());
    }

}
