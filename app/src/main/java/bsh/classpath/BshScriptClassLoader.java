package bsh.classpath;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import bsh.util.DexClassLoaderHelper;
import dalvik.system.InMemoryDexClassLoader;

public class BshScriptClassLoader extends ClassLoader {
    private final DexClassLoaderHelper dexHelper;
    private final Map<String, Class<?>> scriptClassCache = new ConcurrentHashMap<>();

    public BshScriptClassLoader(ClassLoader parent) {
        super(parent);
        this.dexHelper = new DexClassLoaderHelper();
    }

    public Class<?> addClass(String name, byte[] code) {
        try {
            byte[] dexBytes = dexHelper.convertClassToDex(name, code);
            ByteBuffer buffer = ByteBuffer.wrap(dexBytes);
            ClassLoader newLoader = new InMemoryDexClassLoader(buffer, this);
            Class<?> clazz = newLoader.loadClass(name);
            scriptClassCache.put(name, clazz);
            return clazz;
        } catch (Exception e) {
            throw new RuntimeException("Failed to define class " + name + " in script context", e);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = scriptClassCache.get(name);
        if (clazz != null) return clazz;
        return super.loadClass(name, resolve);
    }
}
