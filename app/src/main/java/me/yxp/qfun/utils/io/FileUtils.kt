package me.yxp.qfun.utils.io

import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FileUtils {

    fun ensureDir(dir: File): Boolean {
        return if (!dir.exists()) dir.mkdirs() else dir.isDirectory
    }

    fun ensureFile(file: File): Boolean {
        if (file.exists()) return true
        return try {
            file.parentFile?.let { ensureDir(it) }
            file.createNewFile()
        } catch (_: IOException) {
            false
        }
    }

    fun writeText(file: File, text: String, append: Boolean = false): Boolean {
        if (!ensureFile(file)) return false
        return runCatching {
            if (append) {
                file.appendText(text, Charsets.UTF_8)
            } else {
                file.writeText(text, Charsets.UTF_8)
            }
            true
        }.getOrElse { false }
    }

    fun readText(file: File): String? {
        if (!file.exists()) return null
        return runCatching {
            file.readText(Charsets.UTF_8)
        }.getOrNull()
    }

    fun copy(src: File, dest: File, overwrite: Boolean = true): Boolean {
        if (!src.exists()) return false
        return runCatching {
            src.copyRecursively(dest, overwrite) { _, exception ->
                throw exception
            }
        }.getOrElse { false }
    }

    fun move(src: File, dest: File, overwrite: Boolean = true): Boolean {
        if (!src.exists()) return false
        if (src.renameTo(dest)) return true
        if (copy(src, dest, overwrite)) {
            return delete(src)
        }
        return false
    }

    fun delete(file: File): Boolean {
        if (!file.exists()) return true
        return file.deleteRecursively()
    }

    fun clearDir(dir: File): Boolean {
        if (!dir.exists() || !dir.isDirectory) return false
        var success = true
        dir.listFiles()?.forEach {
            if (!it.deleteRecursively()) success = false
        }
        return success
    }

    fun zip(src: File, destZip: File): Boolean {
        if (!src.exists()) return false
        ensureFile(destZip)

        return runCatching {
            ZipOutputStream(destZip.outputStream().buffered()).use { zos ->
                val baseDir = if (src.isDirectory) src.parentFile else src.parentFile
                src.walkTopDown().forEach { file ->
                    if (file.isFile) {
                        val relativePath = file.relativeTo(baseDir ?: file).path
                        val entry = ZipEntry(relativePath)
                        zos.putNextEntry(entry)
                        file.inputStream().buffered().use { fis ->
                            fis.copyTo(zos)
                        }
                        zos.closeEntry()
                    }
                }
            }
            true
        }.getOrElse {
            delete(destZip)
            false
        }
    }

    fun unzip(zipFile: File, destDir: File): Boolean {
        if (!zipFile.exists()) return false
        ensureDir(destDir)

        return runCatching {
            ZipInputStream(zipFile.inputStream().buffered()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val newFile = File(destDir, entry.name)
                    if (!newFile.canonicalPath.startsWith(destDir.canonicalPath)) {
                        throw SecurityException("Zip Slip vulnerability detected: ${entry.name}")
                    }
                    if (entry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile?.mkdirs()
                        newFile.outputStream().buffered().use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        }.getOrElse { false }
    }
}