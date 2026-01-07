package me.yxp.qfun.plugin.loader

import com.android.dx.cf.direct.DirectClassFile
import com.android.dx.cf.direct.StdAttributeFactory
import com.android.dx.command.dexer.DxContext
import com.android.dx.dex.DexOptions
import com.android.dx.dex.cf.CfOptions
import com.android.dx.dex.cf.CfTranslator
import com.android.dx.dex.file.DexFile
import dalvik.system.PathClassLoader
import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.ClassUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object JarLoader {

    fun loadJar(jarPath: String): ClassLoader {
        val jarFile = File(jarPath)
        if (!jarFile.exists()) {
            throw java.io.FileNotFoundException(jarPath)
        }

        val context = HostInfo.hostContext
        val cacheDir = context.externalCacheDir ?: context.cacheDir

        val jarCacheDir = File(cacheDir, "jars").apply { mkdirs() }
        val dexCacheDir = File(cacheDir, "dex").apply { mkdirs() }

        val cachedJar = File(jarCacheDir, jarFile.name)
        val cachedDex = File(dexCacheDir, jarFile.name.replace(".jar", ".dex"))

        FileUtils.copy(jarFile, cachedJar)
        compileJarToDex(cachedJar, cachedDex)

        return PathClassLoader(cachedDex.absolutePath, ClassUtils.hostClassLoader)
    }

    private fun compileJarToDex(jarFile: File, dexFile: File) {
        val dexOptions = DexOptions()
        val finalDexFile = DexFile(dexOptions)
        val cfOptions = CfOptions()

        ZipInputStream(FileInputStream(jarFile)).use { zis ->
            var entry = zis.nextEntry
            val buffer = ByteArray(4096)

            while (entry != null) {
                if (entry.name.endsWith(".class")) {
                    val baos = ByteArrayOutputStream()
                    var len: Int
                    while (zis.read(buffer).also { len = it } != -1) {
                        baos.write(buffer, 0, len)
                    }
                    val classBytes = baos.toByteArray()

                    processClass(entry.name, classBytes, finalDexFile, dexOptions, cfOptions)
                }
                entry = zis.nextEntry
            }
        }

        FileOutputStream(dexFile).use { fos ->
            finalDexFile.writeTo(fos, null, false)
        }
    }

    private fun processClass(
        name: String,
        bytes: ByteArray,
        dexFile: DexFile,
        dexOptions: DexOptions,
        cfOptions: CfOptions
    ) {
        try {
            val fixName = if (name.endsWith(".class")) name else "$name.class"
            val classFile = DirectClassFile(bytes, fixName, true)
            classFile.setAttributeFactory(StdAttributeFactory.THE_ONE)

            val dxContext = DxContext()
            val classDefItem = CfTranslator.translate(
                dxContext,
                classFile,
                bytes,
                cfOptions,
                dexOptions,
                dexFile
            )
            dexFile.add(classDefItem)
        } catch (_: Exception) {
        }
    }
}