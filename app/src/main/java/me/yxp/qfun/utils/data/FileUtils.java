package me.yxp.qfun.utils.data;

import android.content.Context;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static boolean deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file.getAbsolutePath())) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static boolean copy(String source, String dest) {
        try {
            File destFile = new File(dest);
            File parentDir = destFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (destFile.exists()) {
                destFile.delete();
            }

            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean copySingleFile(Context context, DocumentFile sourceFile, File targetFile) {
        try (InputStream in = context.getContentResolver().openInputStream(sourceFile.getUri());
             OutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void copyDirectoryRecursively(Context context, DocumentFile sourceDir, File targetDir) {
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            return;
        }

        for (DocumentFile sourceItem : sourceDir.listFiles()) {
            if (sourceItem.isDirectory()) {
                File newTargetDir = new File(targetDir, sourceItem.getName());
                copyDirectoryRecursively(context, sourceItem, newTargetDir);
            } else {
                File targetFile = new File(targetDir, sourceItem.getName());
                copySingleFile(context, sourceItem, targetFile);
            }
        }
    }

    public static boolean zipFolders(File outputFile, File... folders) {
        if (folders == null || folders.length == 0) {
            return false;
        }

        // 检查至少有一个文件夹存在
        boolean atLeastOneExists = false;
        for (File folder : folders) {
            if (folder.exists()) {
                atLeastOneExists = true;
                break;
            }
        }

        if (!atLeastOneExists) {
            return false;
        }

        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

            for (int i = 0; i < folders.length; i++) {
                File folder = folders[i];
                if (folder.exists()) {

                    String basePath = folder.getName();
                    addFolderToZip(folder, basePath, zos);
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean zipFolder(File sourceFolder, File outputFile) {
        return zipFolders(outputFile, sourceFolder);
    }

    private static void addFolderToZip(File folder, String parentPath, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            String entryName = parentPath + (parentPath.isEmpty() ? "" : File.separator) + file.getName();

            if (file.isDirectory()) {
                addFolderToZip(file, entryName, zos);
            } else {
                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);

                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, count);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    public static boolean unzip(File zipFile, File targetFolder) {
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            return false;
        }

        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {

            ZipEntry entry;
            byte[] buffer = new byte[1024];

            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(targetFolder, entry.getName());

                if (entry.isDirectory()) {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs();
                    }
                } else {
                    File parent = entryFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(entryFile);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        int count;
                        while ((count = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, count);
                        }
                    }
                }
                zis.closeEntry();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}