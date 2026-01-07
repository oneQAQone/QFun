package bsh.classpath;

import java.nio.ByteBuffer;

import bsh.util.DexClassLoaderHelper;
import dalvik.system.InMemoryDexClassLoader;


public class BshScriptClassLoader extends ClassLoader {

    private final DexClassLoaderHelper dexHelper;
    private ClassLoader chainHead;

    public BshScriptClassLoader(ClassLoader parent) {
        super(parent);
        this.chainHead = parent;
        this.dexHelper = new DexClassLoaderHelper();
    }

    public Class<?> addClass(String name, byte[] code) {
        try {

            byte[] dexBytes = dexHelper.convertClassToDex(name, code);
            ByteBuffer buffer = ByteBuffer.wrap(dexBytes);


            ClassLoader newLoader = new InMemoryDexClassLoader(buffer, this.chainHead);


            Class<?> clazz = newLoader.loadClass(name);


            this.chainHead = newLoader;

            return clazz;
        } catch (Exception e) {
            throw new RuntimeException("Failed to define class " + name + " in script context", e);
        }
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return chainHead.loadClass(name);
    }

    public ClassLoader getChainHead() {
        return chainHead;
    }
}