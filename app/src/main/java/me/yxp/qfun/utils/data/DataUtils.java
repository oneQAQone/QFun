package me.yxp.qfun.utils.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;

public class DataUtils {
    private static String sRootPath;

    private static void setRootPath(String myUin) {
        sRootPath = HostInfo.getMODULE_DATA_PATH() + myUin + "/";
    }

    public static File createFile(String dirs, String filename) {
        setRootPath(QQCurrentEnv.getCurrentUin());

        File dir = new File(sRootPath + dirs + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(sRootPath + dirs + "/" + filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // 忽略创建文件异常
        }

        return file;
    }

    public static void serialize(String dirs, String filename, Object object) {
        setRootPath(QQCurrentEnv.getCurrentUin());

        File file = new File(sRootPath + dirs + "/" + filename);
        if (file.exists()) {
            file.delete();
        }

        createFile(dirs, filename);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        } catch (Exception e) {
            // 忽略序列化异常
        }
    }

    public static Object deserialize(String dirs, String filename) {
        setRootPath(QQCurrentEnv.getCurrentUin());

        File file = new File(sRootPath + dirs + "/" + filename);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return objectInputStream.readObject();
        } catch (Exception e) {
            // 忽略反序列化异常
        }
        return null;
    }
}