package me.yxp.qfun.javaplugin.loader;

import android.app.Activity;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bsh.EvalError;
import me.yxp.qfun.activity.JavaPlugin;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.data.FileUtils;
import me.yxp.qfun.utils.error.PluginError;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.ToastUtils;

public class PluginManager {
    public static List<PluginInfo> pluginInfos = new ArrayList<>();
    public static List<String> autoLoadList;

    public static void getAllPlugin() {
        File pluginDir = new File(HostInfo.getMODULE_DATA_PATH() + QQCurrentEnv.getCurrentUin() + "/plugin");
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
            return;
        }

        List<PluginInfo> currentPlugins = Arrays.stream(pluginDir.listFiles())
                .filter(File::isDirectory)
                .filter(PluginManager::hasRequiredFiles)
                .map(PluginManager::createPluginInfoFromDirectory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        pluginInfos.retainAll(currentPlugins);
        currentPlugins.stream()
                .filter(plugin -> !pluginInfos.contains(plugin))
                .forEach(pluginInfos::add);
    }

    private static PluginInfo createPluginInfoFromDirectory(File pluginDir) {
        try (FileInputStream is = new FileInputStream(new File(pluginDir, "info.prop"));
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            Properties props = new Properties();
            props.load(reader);
            String id = props.getProperty("id");
            if (id == null || id.isEmpty()) {
                return null;
            }
            return new PluginInfo(
                    props.getProperty("pluginName"),
                    props.getProperty("versionCode"),
                    props.getProperty("author"),
                    pluginDir.getAbsolutePath(),
                    id
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean hasRequiredFiles(File pluginDir) {
        return Stream.of("main.java", "info.prop", "desc.txt")
                .allMatch(filename -> new File(pluginDir, filename).isFile());
    }

    public static void autoLoadPlugin() {
        autoLoadList = (List<String>) DataUtils.deserialize("data", "AutoLoadList");
        if (autoLoadList == null) {
            autoLoadList = new ArrayList<>();
        }

        for (PluginInfo pluginInfo : pluginInfos) {
            if (autoLoadList.contains(pluginInfo.pluginId)) {
                new Thread(() -> {
                    try {
                        pluginInfo.pluginCompiler.startPlugin();
                    } catch (EvalError e) {
                        PluginError.evalError(e, pluginInfo);
                    }
                }).start();
            }
        }
    }

    public static void stopAllPlugin() {
        for (PluginInfo pluginInfo : pluginInfos) {
            pluginInfo.pluginCompiler.stopPlugin();
        }
    }

    public static void deletePlugin(PluginInfo pluginInfo, Activity activity) {
        pluginInfo.pluginCompiler.stopPlugin();
        pluginInfos.remove(pluginInfo);
        autoLoadList.remove(pluginInfo.pluginId);

        if (FileUtils.deleteDirectory(pluginInfo.pluginPath)) {
            ((JavaPlugin) activity).notifyDataSetChanged();
        }
    }

    public static void importPlugin(Uri uri, Activity activity) {
        DocumentFile directory = DocumentFile.fromTreeUri(activity, uri);
        if (directory == null || !directory.isDirectory()) {
            ToastUtils.QQToast(1, "导入失败");
            return;
        }

        String pluginPath = HostInfo.getMODULE_DATA_PATH() + QQCurrentEnv.getCurrentUin() + "/plugin/"
                + directory.getName();

        Map<String, DocumentFile> names = new HashMap<>();
        for (DocumentFile file : directory.listFiles()) {
            if (file.isFile()) {
                names.put(file.getName(), file);
            }
        }

        if (names.keySet().containsAll(Arrays.asList("main.java", "info.prop", "desc.txt"))) {
            try (InputStream in = activity.getContentResolver().openInputStream(names.get("info.prop").getUri())) {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));

                PluginInfo pluginInfo = new PluginInfo(
                        properties.get("pluginName").toString(),
                        properties.get("versionCode").toString(),
                        properties.get("author").toString(),
                        pluginPath,
                        properties.get("id").toString()
                );

                FileUtils.copyDirectoryRecursively(activity, directory, new File(pluginPath));
                pluginInfos.add(pluginInfo);
                ((JavaPlugin) activity).notifyDataSetChanged();
                ToastUtils.QQToast(2, "导入成功");
                return;
            } catch (Exception e) {
                ToastUtils.QQToast(1, "导入失败");
            }
        }

        ToastUtils.QQToast(1, "导入失败");
    }
}