package com.tencent.mmkv;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import dalvik.annotation.optimization.FastNative;

public class MMKV implements SharedPreferences, SharedPreferences.Editor {
    public static native long backupAllToDirectory(String str);

    public static native boolean backupOneToDirectory(String str, String str2, @Nullable String str3);

    private static native boolean checkProcessMode(long j);

    private static native long createNB(int i2);

    private static native void destroyNB(long j, int i2);

    private static native long getDefaultMMKV(int i2, @Nullable String str);

    private static native long getMMKVWithAshmemFD(String str, int i2, int i3, @Nullable String str2);

    /**
     * 根据 ID 创建或获取指定的 MMKV 实例
     *
     * @param str  唯一标识符（文件名前缀）
     * @param i2   进程模式（SINGLE_PROCESS / MULTI_PROCESS）
     * @param str2 加密密钥（null 表示不加密）
     * @param str3 自定义存储路径（可为 null，使用默认目录）
     * @param j    预估容量（用于优化 mmap 大小）
     * @return MMKV nativeHandle
     */
    private static native long getMMKVWithID(String str, int i2, @Nullable String str2, @Nullable String str3, long j);

    private static native long getMMKVWithIDAndSize(String str, int i2, int i3, @Nullable String str2);

    public static native boolean isFileValid(String str, @Nullable String str2);

    /**
     * 初始化 MMKV 全局环境。
     *
     * @param str  MMKV 文件存储根目录。
     * @param str2 so 库路径（用于某些平台）。
     * @param i2   日志级别（如 MMKV_LOG_LEVEL_INFO）。
     * @param z    是否重定向日志到 Java 层。
     */
    private static native void jniInitialize(String str, String str2, int i2, boolean z);

    public static native void onExit();

    public static native int pageSize();

    public static native long restoreAllFromDirectory(String str);

    public static native boolean restoreOneMMKVFromDirectory(String str, String str2, @Nullable String str3);

    private static native void setCallbackHandler(boolean z, boolean z2);

    public static native void setLoadOnNecessaryEnable(boolean z);

    private static native void setLogLevel(int i2);

    public static native void setSafeMemoryMask(int i2);

    public static native void setSharedLockFirstWhenReload(boolean z);

    private static native void setWantModeCheck(boolean z);

    private static native void setWantsContentChangeNotify(boolean z);

    public static native String version();

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public Map<String, ?> getAll() {
        return Collections.emptyMap();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return "";
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return Collections.emptySet();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void apply() {

    }

    @Override
    public Editor clear() {
        return null;
    }

    @Override
    public boolean commit() {
        return false;
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        return null;
    }

    @Override
    public Editor putFloat(String key, float value) {
        return null;
    }

    @Override
    public Editor putInt(String key, int value) {
        return null;
    }

    @Override
    public Editor putLong(String key, long value) {
        return null;
    }

    @Override
    public Editor putString(String key, @Nullable String value) {
        return null;
    }

    @Override
    public Editor putStringSet(String key, @Nullable Set<String> values) {
        return null;
    }

    @Override
    public Editor remove(String key) {
        return null;
    }

    private native long actualSize(long j);

    private native String[] allKeys(long j, boolean z);

    private native boolean containsKey(long j, String str);

    private native long count(long j, boolean z);

    private native boolean decodeBool(long j, String str, boolean z);

    @Nullable
    private native byte[] decodeBytes(long j, String str);

    private native double decodeDouble(long j, String str, double d2);

    private native float decodeFloat(long j, String str, float f2);

    private native int decodeInt(long j, String str, int i2);

    private native long decodeLong(long j, String str, long j2);

    @Nullable
    private native String decodeString(long j, String str, @Nullable String str2);

    @Nullable
    private native String[] decodeStringSet(long j, String str);

    private native boolean encodeBool(long j, String str, boolean z);

    private native boolean encodeBool_2(long j, String str, boolean z, int i2);

    private native boolean encodeBytes(long j, String str, @Nullable byte[] bArr);

    private native boolean encodeBytes_2(long j, String str, @Nullable byte[] bArr, int i2);

    private native boolean encodeDouble(long j, String str, double d2);

    private native boolean encodeDouble_2(long j, String str, double d2, int i2);

    private native boolean encodeFloat(long j, String str, float f2);

    private native boolean encodeFloat_2(long j, String str, float f2, int i2);

    private native boolean encodeInt(long j, String str, int i2);

    private native boolean encodeInt_2(long j, String str, int i2, int i3);

    private native boolean encodeLong(long j, String str, long j2);

    private native boolean encodeLong_2(long j, String str, long j2, int i2);

    private native boolean encodeSet(long j, String str, @Nullable String[] strArr);

    private native boolean encodeSet_2(long j, String str, @Nullable String[] strArr, int i2);

    private native boolean encodeString(long j, String str, @Nullable String str2);

    private native boolean encodeString_2(long j, String str, @Nullable String str2, int i2);

    private native boolean isCompareBeforeSetEnabled();

    @FastNative
    private native boolean isEncryptionEnabled();

    @FastNative
    private native boolean isExpirationEnabled();

    @FastNative
    private native void nativeEnableCompareBeforeSet();

    private native void removeValueForKey(long j, String str);

    private native void sync(boolean z);

    private native long totalSize(long j);

    private native int valueSize(long j, String str, boolean z);

    private native int writeValueToNB(long j, String str, long j2, int i2);

    public native int ashmemFD();

    public native int ashmemMetaFD();

    public native void checkContentChangedByOuterProcess();

    public native void checkReSetCryptKey(@Nullable String str);

    public native void clearAll();

    public native void clearAllWithKeepingSpace();

    public native void clearMemoryCache();

    public native void close();

    @Nullable
    public native String cryptKey();

    public native boolean disableAutoKeyExpire();

    public native void disableCompareBeforeSet();

    public native boolean enableAutoKeyExpire(int i2);

    public native void lock();

    public native String mmapID();

    public native boolean reKey(@Nullable String str);

    public native void removeValuesForKeys(String[] strArr);

    public native void trim();

    public native boolean tryLock();

    public native void unlock();
}
