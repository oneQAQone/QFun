package me.yxp.qfun.javaplugin.loader;

import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.PathClassLoader;
import me.yxp.qfun.utils.data.FileUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.reflect.ClassUtils;

public class LoadJarHelper {

    public static ClassLoader loadJar(String jarPath) throws Exception {
        File jarFile = new File(jarPath);
        if (!jarFile.exists()) {
            throw new FileNotFoundException("Can't find jar file:" + jarPath);
        }

        String jarCachePath = HostInfo.getHostContext().getExternalCacheDir() + "/jars/" + jarFile.getName();
        String dexCache = HostInfo.getHostContext().getExternalCacheDir() + "/dex/"
                + jarFile.getName().replace(".jar", ".dex");

        FileUtils.copy(jarPath, jarCachePath);
        compileJarToDex(jarCachePath, dexCache);

        FixJarClassLoader fixLoader = new FixJarClassLoader(ClassUtils.getHostClassLoader());
        return new PathClassLoader(dexCache, fixLoader);
    }

    private static void compileJarToDex(String jarPath, String dest) throws Exception {
        DexOptions dexOptions = new DexOptions();
        DexFile newDexFile = new DexFile(dexOptions);

        try (ZipInputStream zInp = new ZipInputStream(new FileInputStream(jarPath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zInp.getNextEntry()) != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;

                while ((read = zInp.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

                byte[] classCode = out.toByteArray();
                String className = zipEntry.getName();
                DxContext dxContext = new DxContext();

                if (className.endsWith(".class")) {
                    DirectClassFile classFile = new DirectClassFile(classCode, className, true);
                    classFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
                    newDexFile.add(CfTranslator.translate(dxContext, classFile, classCode,
                            new CfOptions(), dexOptions, newDexFile));
                }
            }

            ByteArrayOutputStream destByte = new ByteArrayOutputStream();
            newDexFile.writeTo(destByte, null, false);

            File parent = new File(dest).getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            try (FileOutputStream fOut = new FileOutputStream(dest)) {
                fOut.write(destByte.toByteArray());
            }
        }
    }

    private static class FixJarClassLoader extends ClassLoader {
        private final ClassLoader mParent;

        public FixJarClassLoader(ClassLoader parent) {
            mParent = parent;
        }
    }
}