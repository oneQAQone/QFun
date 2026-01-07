package bsh.classpath;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

import bsh.util.DataUtil;
import dalvik.system.InMemoryDexClassLoader;

public class BshLoaderManager {
    private static final HashSet<ClassLoader> loaders = new HashSet<>();
    private static final HashMap<String, ClassLoader> dexLoaders = new HashMap<>();

    public static void addLoader(ClassLoader loader) {
        if (loader != null) loaders.add(loader);
    }

    public static ClassLoader getDexLoader(String dexPath, ClassLoader parentLoader) {
        String dexMd5 = DataUtil.getFileMD5(dexPath);
        if (dexLoaders.containsKey(dexMd5)) return dexLoaders.get(dexMd5);
        try {
            File dexFile = new File(dexPath);
            byte[] dexBytes = Files.readAllBytes(dexFile.toPath());
            ClassLoader dexLoader = new InMemoryDexClassLoader(ByteBuffer.wrap(dexBytes), parentLoader);
            dexLoaders.put(dexMd5, dexLoader);
            return dexLoader;
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getClass(String name) {
        for (ClassLoader loader : loaders) {
            try {
                return loader.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }
}